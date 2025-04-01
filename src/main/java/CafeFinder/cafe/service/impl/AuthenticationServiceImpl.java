package CafeFinder.cafe.service.impl;

import static CafeFinder.cafe.infrastructure.jwt.JwtMessage.GENERATE_ACCESSTOKEN;

import CafeFinder.cafe.dto.MemberLoginDto;
import CafeFinder.cafe.dto.RefreshTokenDto;
import CafeFinder.cafe.dto.TokenRequestDto;
import CafeFinder.cafe.dto.TokenResultDto;
import CafeFinder.cafe.exception.IncorrectPasswordException;
import CafeFinder.cafe.infrastructure.jwt.AccesTokenInfoDto;
import CafeFinder.cafe.infrastructure.jwt.TokenDto;
import CafeFinder.cafe.infrastructure.jwt.TokenProvider;
import CafeFinder.cafe.infrastructure.redis.RefreshTokenService;
import CafeFinder.cafe.service.interfaces.AuthenticationService;
import CafeFinder.cafe.service.interfaces.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private static final String UNAUTHORIZED_MESSAGE = "인증되지 않은 사용자";

    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;

    @Override
    @Transactional
    public TokenDto login(MemberLoginDto loginDto) {
        Authentication authentication = authenticateUser(loginDto);
        log.info("로그인 성공: 이메일={}", loginDto.getEmail());
        return tokenService.generateToken(authentication);
    }

    @Override
    @Transactional
    public void logout(RefreshTokenDto refreshTokenDto) {
        log.info("로그아웃 시도");
        refreshTokenService.addTokenToBlacklist(refreshTokenDto.getRefreshToken());
        SecurityContextHolder.clearContext();
        log.info("로그아웃 완료: 인증 정보 삭제");
    }

    @Override
    public TokenResultDto validateToken(TokenRequestDto tokenRequestDto) {
        if (isAccessTokenValid(tokenRequestDto.getAccessToken())) {
            return buildAccessTokenValidResult();
        }

        if (isRefreshTokenInvalid(tokenRequestDto.getRefreshToken())) {
            return buildInvalidTokenResult();
        }

        if (isRefreshTokenBlacklisted(tokenRequestDto.getRefreshToken())) {
            log.warn("리프레시 토큰이 블랙리스트에 존재합니다: {}", tokenRequestDto.getRefreshToken());
            return buildInvalidTokenResult();
        }

        var userDetails = getUserDetailsFromRefreshToken(tokenRequestDto.getRefreshToken());
        if (userDetails == null) {
            return buildInvalidTokenResult();
        }

        return generateNewAccessToken(userDetails);
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

    private TokenResultDto buildAccessTokenValidResult() {
        return TokenResultDto.builder()
                .isAccessTokenValid(true)
                .isRefreshTokenValid(false)
                .message("유효한 AccessToken")
                .newAccessToken(null)
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

    private UserDetails getUserDetailsFromRefreshToken(String refreshToken) {
        return tokenProvider.getUserDetailsFromRefreshToken(refreshToken);
    }

    private TokenResultDto generateNewAccessToken(UserDetails userDetails) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        log.info("액세스 토큰이 없거나 만료됨, 리프레시 토큰이 유효: 새로운 액세스 토큰 발급");
        AccesTokenInfoDto newAccessToken = generateAccessToken(authentication);
        return TokenResultDto.builder()
                .isAccessTokenValid(false)
                .isRefreshTokenValid(true)
                .message(GENERATE_ACCESSTOKEN.getMessage())
                .newAccessToken(newAccessToken)
                .build();
    }

    private AccesTokenInfoDto generateAccessToken(Authentication authentication) {
        TokenDto tokenDto = tokenService.generateToken(authentication);
        return AccesTokenInfoDto.builder()
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .accessTokenExpiresIn(tokenDto.getAccessTokenExpiresIn())
                .build();
    }

}
