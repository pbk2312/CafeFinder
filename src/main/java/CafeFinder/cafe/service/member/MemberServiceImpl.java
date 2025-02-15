package CafeFinder.cafe.service.member;

import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.dto.MemberLoginDto;
import CafeFinder.cafe.dto.MemberSignUpDto;
import CafeFinder.cafe.dto.TokenResultDto;
import CafeFinder.cafe.exception.IncorrectPasswordException;
import CafeFinder.cafe.exception.MemberNotFoundException;
import CafeFinder.cafe.exception.YetVerifyEmailException;
import CafeFinder.cafe.jwt.AccesTokenDto;
import CafeFinder.cafe.jwt.TokenDto;
import CafeFinder.cafe.jwt.TokenProvider;
import CafeFinder.cafe.repository.MemberRepository;
import CafeFinder.cafe.service.redis.RefreshTokenService;
import CafeFinder.cafe.validator.MemberValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final RefreshTokenService refreshTokenService;


    @Override
    @Transactional
    public void save(MemberSignUpDto memberSignUpDto) {
        log.info("회원가입 시작: 이메일={}", memberSignUpDto.getEmail());

        // 이메일 인증 여부 확인
        isEmailVerify(memberSignUpDto);

        // 비밀번호 검증
        MemberValidator.validatePassword(memberSignUpDto.getPassword(), memberSignUpDto.getCheckPassword());

        // 회원 생성 및 저장
        Member member = Member.create(memberSignUpDto, passwordEncoder.encode(memberSignUpDto.getPassword()));
        memberRepository.save(member);

        // 이메일 인증 데이터 삭제
        deleteEmailVerification(memberSignUpDto.getEmail());

        log.info("회원가입 성공: 이메일={}", memberSignUpDto.getEmail());
    }

    private void deleteEmailVerification(String email) {
        String key = "verified:" + email;
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
            log.info("Redis 인증 데이터 삭제 완료: 이메일={}", email);
        } else {
            log.warn("Redis 인증 데이터 없음: 이메일={}", email);
        }
    }

    private void isEmailVerify(MemberSignUpDto memberSignUpDto) {
        String isVerified = redisTemplate.opsForValue().get("verified:" + memberSignUpDto.getEmail());
        if (!"true".equals(isVerified)) {
            log.warn("회원가입 실패: 이메일 인증 미완료 이메일={}", memberSignUpDto.getEmail());
            throw new YetVerifyEmailException();
        }
    }

    @Override
    @Transactional
    public AccesTokenDto login(MemberLoginDto memberLoginDto) {
        log.info("로그인 시작: 이메일={}", memberLoginDto.getEmail());

        // 회원 조회
        Member member = findMemberByEmail(memberLoginDto.getEmail());

        // 사용자 인증
        Authentication authentication = authenticateUser(memberLoginDto);

        // 토큰 발급
        TokenDto tokenDto = getTokenDto(authentication);

        // redis에 refreshToken 저장
        refreshTokenService.saveRefreshToken(member.getId(), tokenDto.getRefreshToken(),
                tokenDto.getRefreshTokenExpiresIn());

        log.info("로그인 성공: 이메일={}", memberLoginDto.getEmail());

        // accessToken 반환
        return generateAccessTokenDto(authentication);
    }

    @Override
    @Transactional
    public void logout() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = findMemberByEmail(email);

        // SecurityContextHolder에서 인증 정보 삭제
        SecurityContextHolder.clearContext();
        refreshTokenService.deleteRefreshToken(member.getId());

        log.info("로그아웃 완료: 인증 정보 삭제");

    }

    @Override
    public TokenResultDto validateToken(String accessToken) {

        // 1. 액세스 토큰 검증
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        boolean isAccessTokenValid = tokenProvider.validate(accessToken);

        if (isAccessTokenValid) {
            return TokenResultDto.builder()
                    .isAccessTokenValid(true)
                    .isRefreshTokenValid(false)
                    .message("유효한 AccessToken")
                    .newAccessToken(null)
                    .build();
        }

        // 2. 액세스 토큰이 만료되었으므로, Redis에서 리프레시 토큰 확인
        Member member;
        try {
            member = findMemberByAuthentication(authentication);
        } catch (MemberNotFoundException e) {
            log.warn("인증되지 않은 사용자: authentication={}", authentication);
            return TokenResultDto.builder()
                    .isAccessTokenValid(false)
                    .isRefreshTokenValid(false)
                    .newAccessToken(null)
                    .message("인증되지 않은 사용자")
                    .build();
        }

        String refreshToken = refreshTokenService.getRefreshToken(member.getId());
        if (refreshToken == null) {
            log.warn("유효한 리프레시 토큰 없음: memberId={}", member.getId());
            return TokenResultDto.builder()
                    .isAccessTokenValid(false)
                    .isRefreshTokenValid(false)
                    .newAccessToken(null)
                    .message("인증되지 않은 사용자")
                    .build();
        }

        // 3. 리프레시 토큰 유효성 검증
        boolean isRefreshTokenValid = tokenProvider.validate(refreshToken);
        if (!isRefreshTokenValid) {
            log.warn("리프레시 토큰 유효하지 않음: memberId={}", member.getId());
            return TokenResultDto.builder()
                    .isAccessTokenValid(false)
                    .isRefreshTokenValid(false)
                    .newAccessToken(null)
                    .message("인증되지 않은 사용자")
                    .build();
        }

        // 4. 리프레시 토큰이 유효하므로 새로운 액세스 토큰 발급
        log.info("액세스 토큰 만료됨, 하지만 리프레시 토큰이 유효함: 새로운 액세스 토큰 필요");
        AccesTokenDto newAccessToken = generateAccessTokenDto(authentication);

        return TokenResultDto.builder()
                .isAccessTokenValid(false)
                .isRefreshTokenValid(true)
                .message("액세스 토큰 만료됨, 새로운 액세스 토큰 발급 완료")
                .newAccessToken(newAccessToken)
                .build();
    }

    private Member findMemberByAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return findMemberByEmail(userDetails.getUsername());
    }

    private AccesTokenDto generateAccessTokenDto(Authentication authentication) {
        TokenDto tokenDto = getTokenDto(authentication);
        return AccesTokenDto.builder()
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .accessTokenExpiresIn(tokenDto.getAccessTokenExpiresIn()).build();
    }

    private TokenDto getTokenDto(Authentication authentication) {
        return tokenProvider.generateTokenDto(authentication);
    }


    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("회원 조회 실패: 이메일={}", email);
                    return new MemberNotFoundException();
                });
    }

    private Authentication authenticateUser(MemberLoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken = loginDto.toAuthentication();
        try {
            return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            log.error("로그인 실패: 잘못된 비밀번호, 이메일={}", loginDto.getEmail());
            throw new IncorrectPasswordException();
        }
    }

}
