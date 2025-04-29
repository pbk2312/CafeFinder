package CafeFinder.cafe.member.service;

import CafeFinder.cafe.global.infrastructure.redis.RefreshTokenService;
import CafeFinder.cafe.member.dto.AccesTokenDto;
import CafeFinder.cafe.member.dto.RefreshTokenDto;
import CafeFinder.cafe.member.dto.TokenDto;
import CafeFinder.cafe.member.security.jwt.JwtAuthenticationProvider;
import CafeFinder.cafe.member.security.jwt.JwtValidator;
import CafeFinder.cafe.member.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final TokenProvider tokenProvider;
    private final JwtValidator jwtValidator;
    private final RefreshTokenService refreshTokenService;
    private final JwtAuthenticationProvider authenticationProvider;

    @Override
    public TokenDto generateToken(Authentication authentication) {
        return tokenProvider.generateTokenDto(authentication);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtValidator.validate(token);
    }

    @Override
    public AccesTokenDto reIssueToken(RefreshTokenDto refreshTokenDto) {
        if (isRefreshTokenInvalid(getRefreshToken(refreshTokenDto))) {
            removeLogin(getRefreshToken(refreshTokenDto));
            return null;
        }

        if (isRefreshTokenBlacklisted(getRefreshToken(refreshTokenDto))) {
            removeLogin(getRefreshToken(refreshTokenDto));
            return null;
        }

        var userDetails = getUserDetailsFromRefreshToken(getRefreshToken(refreshTokenDto));

        if (userDetails == null) {
            removeLogin(getRefreshToken(refreshTokenDto));
            return null;
        }

        Authentication authentication = getAuthentication(userDetails);

        return generateAccessToken(authentication);

    }

    private void removeLogin(String refreshToken) {
        SecurityContextHolder.clearContext();
        refreshTokenService.addTokenToBlacklist(refreshToken);
    }


    private static UsernamePasswordAuthenticationToken getAuthentication(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }


    private static String getRefreshToken(RefreshTokenDto refreshTokenDto) {
        return refreshTokenDto.getRefreshToken();
    }

    private AccesTokenDto generateAccessToken(Authentication authentication) {
        TokenDto tokenDto = generateToken(authentication);
        return AccesTokenDto.builder()
                .accessToken(tokenDto.getAccessToken())
                .accessTokenExpiresIn(tokenDto.getAccessTokenExpiresIn())
                .build();
    }


    private boolean isRefreshTokenBlacklisted(String refreshToken) {
        return refreshTokenService.isTokenBlacklisted(refreshToken);
    }

    private boolean isRefreshTokenInvalid(String refreshToken) {
        return refreshToken == null
                || refreshToken.isEmpty()
                || !validateToken(refreshToken);
    }

    private UserDetails getUserDetailsFromRefreshToken(String refreshToken) {
        return authenticationProvider.getUserDetailsFromRefreshToken(refreshToken);
    }

}
