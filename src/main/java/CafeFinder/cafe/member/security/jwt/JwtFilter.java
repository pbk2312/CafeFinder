package CafeFinder.cafe.member.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";

    private final JwtValidator jwtValidator;
    private final JwtAuthenticationProvider authenticationProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        log.info("Request ID: {} - 요청 URI: {}에 대한 JWT 필터 시작", requestId, request.getRequestURI());

        String jwt = resolveToken(request);
        if (jwt == null) {
            log.debug("Request ID: {} - 요청 헤더와 쿠키에 JWT 토큰이 없음", requestId);
        } else {
            log.debug("Request ID: {} - JWT 토큰 발견: {}", requestId, jwt);
        }

        if (StringUtils.hasText(jwt) && jwtValidator.validate(jwt)) {
            Authentication authentication = authenticationProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Request ID: {} - 유효한 JWT 토큰. 인증 정보 설정: {}", requestId, authentication.getName());
        } else {
            if (StringUtils.hasText(jwt)) {
                log.info("Request ID: {} - 유효하지 않은 JWT 토큰", requestId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 JWT 토큰");
                return;
            }
        }

        filterChain.doFilter(request, response);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Request ID: {} - 요청 URI: {}에 대한 JWT 필터 종료. 처리 시간: {} ms", requestId, request.getRequestURI(),
                duration);
    }

    private String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}


