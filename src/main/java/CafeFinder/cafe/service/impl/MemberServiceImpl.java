package CafeFinder.cafe.service.impl;

import static CafeFinder.cafe.infrastructure.jwt.JwtMessage.GENERATE_ACCESSTOKEN;

import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.dto.MemberLoginDto;
import CafeFinder.cafe.dto.MemberProfileDto;
import CafeFinder.cafe.dto.MemberSignUpDto;
import CafeFinder.cafe.dto.MemberUpdateDto;
import CafeFinder.cafe.dto.ProfileDto;
import CafeFinder.cafe.dto.TokenResultDto;
import CafeFinder.cafe.exception.IncorrectPasswordException;
import CafeFinder.cafe.exception.MemberNotFoundException;
import CafeFinder.cafe.exception.YetVerifyEmailException;
import CafeFinder.cafe.infrastructure.jwt.AccesTokenDto;
import CafeFinder.cafe.infrastructure.jwt.TokenDto;
import CafeFinder.cafe.infrastructure.jwt.TokenProvider;
import CafeFinder.cafe.infrastructure.redis.RedisEmailVerifyService;
import CafeFinder.cafe.infrastructure.redis.RefreshTokenService;
import CafeFinder.cafe.repository.MemberRepository;
import CafeFinder.cafe.service.interfaces.FileService;
import CafeFinder.cafe.service.interfaces.MemberService;
import CafeFinder.cafe.service.interfaces.TokenService;
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
    private final RedisEmailVerifyService redisEmailVerifyService;
    private final FileService fileService;
    private final TokenProvider tokenProvider;


    @Value("${cafe.profile.image.base-path:/Users/park/}")
    private String profileImageBasePath;

    @Value("${cafe.profile.image.relative-path:/img/}")
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
    public MemberProfileDto getUserInfoByToken(String accessToken) {
        Member member = getMemberByToken(accessToken);
        String relativePath = convertToRelativePath(member.getProfileImagePath());
        return MemberProfileDto.builder()
                .nickName(member.getNickName())
                .profileImagePath(relativePath)
                .build();
    }

    @Override
    @Transactional
    public TokenDto login(MemberLoginDto loginDto) {
        Authentication authentication = authenticateUser(loginDto);
        log.info("로그인 성공: 이메일={}", loginDto.getEmail());
        return tokenService.generateToken(authentication);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        log.info("로그아웃 시도");
        refreshTokenService.addTokenToBlacklist(refreshToken);
        SecurityContextHolder.clearContext();
        log.info("로그아웃 완료: 인증 정보 삭제");
    }


    @Override
    @Transactional
    public void update(MemberUpdateDto updateDto, String accessToken) {
        Member member = getMemberByToken(accessToken);
        updateMemberProfile(member, updateDto);
        log.info("멤버 정보 수정 성공: {}", updateDto.getNickName());
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

    @Override
    public TokenResultDto validateToken(String accessToken, String refreshToken) {
        if (isAccessTokenValid(accessToken)) {
            return buildAccessTokenValidResult();
        }

        if (isRefreshTokenInvalid(refreshToken)) {
            return buildInvalidTokenResult();
        }

        if (isRefreshTokenBlacklisted(refreshToken)) {
            log.warn("리프레시 토큰이 블랙리스트에 존재합니다: {}", refreshToken);
            return buildInvalidTokenResult();
        }

        var userDetails = getUserDetailsFromRefreshToken(refreshToken);
        if (userDetails == null) {
            return buildInvalidTokenResult();
        }

        return generateNewAccessToken(userDetails);
    }

    private boolean isAccessTokenValid(String accessToken) {
        return accessToken != null
                && !accessToken.isEmpty()
                && tokenService.validateToken(accessToken);
    }

    private boolean isRefreshTokenInvalid(String refreshToken) {
        return refreshToken == null
                || refreshToken.isEmpty()
                || !tokenService.validateToken(refreshToken);
    }

    private boolean isRefreshTokenBlacklisted(String refreshToken) {
        return refreshTokenService.isTokenBlacklisted(refreshToken);
    }

    private UserDetails getUserDetailsFromRefreshToken(String refreshToken) {
        return tokenProvider.getUserDetailsFromRefreshToken(refreshToken);
    }

    private TokenResultDto generateNewAccessToken(UserDetails userDetails) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        log.info("액세스 토큰이 없거나 만료됨, 리프레시 토큰이 유효: 새로운 액세스 토큰 발급");
        AccesTokenDto newAccessToken = generateAccessToken(authentication);
        return TokenResultDto.builder()
                .isAccessTokenValid(false)
                .isRefreshTokenValid(true)
                .message(GENERATE_ACCESSTOKEN.getMessage())
                .newAccessToken(newAccessToken)
                .build();
    }

    private TokenResultDto buildAccessTokenValidResult() {
        return TokenResultDto.builder()
                .isAccessTokenValid(true)
                .isRefreshTokenValid(false)
                .message("유효한 AccessToken")
                .newAccessToken(null)
                .build();
    }

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

    private void updateMemberProfile(Member member, MemberUpdateDto updateDto) {
        if (updateDto.getNewProfileImage() != null && !updateDto.getNewProfileImage().isEmpty()) {
            fileService.deleteProfileImage(member.getProfileImagePath());
            String newImagePath = fileService.saveProfileImage(updateDto.getNewProfileImage());
            member.updateProfile(updateDto.getNickName(), newImagePath);
        } else {
            member.updateProfile(updateDto.getNickName(), member.getProfileImagePath());
        }
    }

}

