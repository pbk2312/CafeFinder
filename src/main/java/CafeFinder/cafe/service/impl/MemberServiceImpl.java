package CafeFinder.cafe.service.impl;

import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.dto.MemberLoginDto;
import CafeFinder.cafe.dto.MemberSignUpDto;
import CafeFinder.cafe.dto.ProfileDto;
import CafeFinder.cafe.dto.TokenResultDto;
import CafeFinder.cafe.dto.UserInfoDto;
import CafeFinder.cafe.dto.UserUpdateDto;
import CafeFinder.cafe.exception.IncorrectPasswordException;
import CafeFinder.cafe.exception.MemberNotFoundException;
import CafeFinder.cafe.exception.YetVerifyEmailException;
import CafeFinder.cafe.jwt.AccesTokenDto;
import CafeFinder.cafe.jwt.TokenDto;
import CafeFinder.cafe.repository.MemberRepository;
import CafeFinder.cafe.service.interfaces.MemberService;
import CafeFinder.cafe.service.interfaces.TokenService;
import CafeFinder.cafe.service.member.FileService;
import CafeFinder.cafe.service.redis.RedisEmailVerifyService;
import CafeFinder.cafe.service.redis.RefreshTokenService;
import static CafeFinder.cafe.util.ViewMessage.GENERATE_ACCESSTOKEN;
import CafeFinder.cafe.validator.MemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private static final String UNAUTHORIZED_MESSAGE = "인증되지 않은 사용자";

    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final RefreshTokenService refreshTokenService;
    private final FileService fileService;
    private final RedisEmailVerifyService redisEmailVerifyService;

    
    @Value("${cafe.profile.image.base-path:/Users/park/}")
    private String profileImageBasePath;

    @Value("${cafe.profile.image.relative-path:/img/profile/}")
    private String profileImageRelativePath;

    @Override
    @Transactional
    public void save(MemberSignUpDto signUpDto) {
        log.info("회원가입 시작: 이메일={}", signUpDto.getEmail());
        verifyEmailAuthentication(signUpDto);
        MemberValidator.validatePassword(signUpDto.getPassword(), signUpDto.getCheckPassword());
        String profileImagePath = fileService.saveProfileImage(signUpDto.getProfileImage());
        Member member = Member.create(signUpDto, passwordEncoder.encode(signUpDto.getPassword()), profileImagePath);
        memberRepository.save(member);
        redisEmailVerifyService.deleteVerificationCode(signUpDto.getEmail());
        log.info("회원가입 성공: 이메일={}", signUpDto.getEmail());
    }

    @Override
    public UserInfoDto getUserInfoByToken(String accessToken) {
        Member member = getMemberByToken(accessToken);
        log.info("토큰 멤버 조회: {}", member.getEmail());
        String relativePath = convertToRelativePath(member.getProfileImagePath());
        return UserInfoDto.builder()
                .nickName(member.getNickName())
                .profileImagePath(relativePath)
                .build();
    }

    @Override
    @Transactional
    public AccesTokenDto login(MemberLoginDto loginDto) {
        log.info("로그인 시작: 이메일={}", loginDto.getEmail());
        Member member = getMemberByEmail(loginDto.getEmail());
        Authentication authentication = authenticateUser(loginDto);
        TokenDto tokenDto = tokenService.generateToken(authentication);
        refreshTokenService.saveRefreshToken(member.getId(), tokenDto.getRefreshToken(),
                tokenDto.getRefreshTokenExpiresIn());
        log.info("로그인 성공: 이메일={}", loginDto.getEmail());
        return generateAccessToken(authentication);
    }

    @Override
    @Transactional
    public void logout() {
        log.info("로그아웃 시도");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Member member = getMemberByEmail(email);
        SecurityContextHolder.clearContext();
        refreshTokenService.deleteRefreshToken(member.getId());
        log.info("로그아웃 완료: 인증 정보 삭제");
    }

    @Override
    public TokenResultDto validateToken(String accessToken) {
        Authentication authentication = tokenService.getAuthentication(accessToken);
        if (tokenService.validateToken(accessToken)) {
            return TokenResultDto.builder()
                    .isAccessTokenValid(true)
                    .isRefreshTokenValid(false)
                    .message("유효한 AccessToken")
                    .newAccessToken(null)
                    .build();
        }

        Member member = getMemberFromAuthentication(authentication);
        String refreshToken = refreshTokenService.getRefreshToken(member.getId());
        if (refreshToken == null || !tokenService.validateToken(refreshToken)) {
            return buildInvalidTokenResult();
        }

        log.info("액세스 토큰 만료됨, 리프레시 토큰이 유효: 새로운 액세스 토큰 발급");
        AccesTokenDto newAccessToken = generateAccessToken(authentication);
        return TokenResultDto.builder()
                .isAccessTokenValid(false)
                .isRefreshTokenValid(true)
                .message(GENERATE_ACCESSTOKEN.getMessage())
                .newAccessToken(newAccessToken)
                .build();
    }

    @Override
    @Transactional
    public void update(UserUpdateDto updateDto, String accessToken) {
        log.info("멤버 정보 수정: {}", updateDto.getNickName());
        Member member = getMemberByToken(accessToken);
        updateMemberProfile(member, updateDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileDto getProfileByToken(String accessToken) {
        Member member = getMemberByToken(accessToken);
        String relativePath = convertToRelativePath(member.getProfileImagePath());
        return ProfileDto.builder()
                .email(member.getEmail())
                .nickName(member.getNickName())
                .memberRole(member.getMemberRole())
                .provider(member.getProvider())
                .profileImagePath(relativePath)
                .build();
    }

    @Override
    public Member getMemberByToken(String accessToken) {
        Authentication authentication = tokenService.getAuthentication(accessToken);
        return getMemberFromAuthentication(authentication);
    }

    @Override
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("회원 조회 실패: 이메일={}", email);
                    return new MemberNotFoundException();
                });
    }

    /* ------------------------- 내부 헬퍼 메소드 ------------------------- */

    private void verifyEmailAuthentication(MemberSignUpDto signUpDto) {
        String isVerified = redisTemplate.opsForValue().get("verified:" + signUpDto.getEmail());
        if (!"true".equals(isVerified)) {
            log.warn("회원가입 실패: 이메일 인증 미완료, 이메일={}", signUpDto.getEmail());
            throw new YetVerifyEmailException();
        }
    }

    private Member getMemberFromAuthentication(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return getMemberByEmail(userDetails.getUsername());
        } catch (Exception e) {
            log.warn("인증된 사용자를 찾을 수 없음: authentication={}", authentication);
            throw new MemberNotFoundException();
        }
    }

    private AccesTokenDto generateAccessToken(Authentication authentication) {
        TokenDto tokenDto = tokenService.generateToken(authentication);
        return AccesTokenDto.builder()
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .accessTokenExpiresIn(tokenDto.getAccessTokenExpiresIn())
                .build();
    }

    private TokenResultDto buildInvalidTokenResult() {
        return TokenResultDto.builder()
                .isAccessTokenValid(false)
                .isRefreshTokenValid(false)
                .newAccessToken(null)
                .message(UNAUTHORIZED_MESSAGE)
                .build();
    }

    // 외부 설정된 base 경로를 기준으로 상대 경로를 생성하도록 수정
    private String convertToRelativePath(String path) {
        if (path != null && path.startsWith(profileImageBasePath)) {
            return profileImageRelativePath + path.substring(path.lastIndexOf("/") + 1);
        }
        return path;
    }

    private Authentication authenticateUser(MemberLoginDto loginDto) {
        UsernamePasswordAuthenticationToken authToken = loginDto.toAuthentication();
        try {
            return authenticationManagerBuilder.getObject().authenticate(authToken);
        } catch (BadCredentialsException e) {
            log.error("로그인 실패: 잘못된 비밀번호, 이메일={}", loginDto.getEmail());
            throw new IncorrectPasswordException();
        }
    }

    private void updateMemberProfile(Member member, UserUpdateDto updateDto) {
        if (updateDto.getNewProfileImage() != null && !updateDto.getNewProfileImage().isEmpty()) {
            fileService.deleteProfileImage(member.getProfileImagePath());
            String newImagePath = fileService.saveProfileImage(updateDto.getNewProfileImage());
            member.updateProfile(updateDto.getNickName(), newImagePath);
        } else {
            member.updateProfile(updateDto.getNickName(), member.getProfileImagePath());
        }
    }

}

