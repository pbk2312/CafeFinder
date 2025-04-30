package CafeFinder.cafe.member.security.jwt;

import CafeFinder.cafe.global.infrastructure.redis.RefreshTokenService;
import CafeFinder.cafe.member.dto.TokenDto;
import CafeFinder.cafe.member.util.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final JwtValidator jwtValidator;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final JwtAuthenticationProvider authenticationProvider;


    private static final List<String> skipPaths = List.of(
            "/member/signupAndLogin",
            "/api/member/signUp",
            "/api/member/login"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return skipPaths.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("Refresh token 필터 시작");

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            log.info("이미 인증된 사용자...");
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = getCookieValue(request, ACCESS_TOKEN_COOKIE_NAME);
        if (accessToken != null && jwtValidator.isExpired(accessToken)) {
            log.info("토큰 재발급..");
            String refreshToken = getCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);

            if (isRefreshTokenInvalid(refreshToken) || isRefreshTokenBlacklisted(refreshToken)) {
                removeLogin(refreshToken);
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = getUserDetailsFromRefreshToken(refreshToken);
            if (userDetails == null) {
                removeLogin(refreshToken);
                filterChain.doFilter(request, response);
                return;
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
            CookieUtils.addCookie(response,
                    ACCESS_TOKEN_COOKIE_NAME, tokenDto.getAccessToken(), tokenDto.getAccessTokenExpiresIn());
            CookieUtils.addCookie(response,
                    REFRESH_TOKEN_COOKIE_NAME, tokenDto.getRefreshToken(), tokenDto.getRefreshTokenExpiresIn());
        }

        filterChain.doFilter(request, response);
    }

    private UserDetails getUserDetailsFromRefreshToken(String refreshToken) {
        return authenticationProvider.getUserDetailsFromRefreshToken(refreshToken);
    }

    private boolean isRefreshTokenInvalid(String refreshToken) {
        return refreshToken == null
                || refreshToken.isEmpty()
                || !jwtValidator.validate(refreshToken);
    }

    private boolean isRefreshTokenBlacklisted(String refreshToken) {
        return refreshTokenService.isTokenBlacklisted(refreshToken);
    }

    private void removeLogin(String refreshToken) {
        SecurityContextHolder.clearContext();
        refreshTokenService.addTokenToBlacklist(refreshToken);
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

}
