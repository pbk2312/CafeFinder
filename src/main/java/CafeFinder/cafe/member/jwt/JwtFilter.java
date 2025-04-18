package CafeFinder.cafe.member.jwt;

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

    private final TokenProvider tokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 필터를 반드시 타야 하는 조건들만
        boolean isCafeClick = method.equals("POST") && uri.matches("^/api/cafes/click/[^/]+$");
        boolean isMemberLogout = method.equals("POST") && uri.equals("/api/member/logout");
        boolean isMemberUpdate = method.equals("PATCH") && uri.equals("/api/member/update");
        boolean isMemberProfile = method.equals("GET") && uri.equals("/api/member/profile");
        boolean isMemberRecommand = method.equals("GET") && uri.equals("/api/member/getRecommandCafes");
        boolean isMemberScrap = method.equals("POST") && uri.matches("^/api/member/[^/]+/scrap$");
        boolean isMemberProfilePage = method.equals("GET") && uri.equals("/member/profile");
        boolean isMemberEditPage = method.equals("GET") && uri.equals("/member/edit");
        boolean isGetScrapCafes = method.equals("GET") && uri.equals("/api/member/scrapCafes");

        boolean shouldFilter = isCafeClick ||
                isMemberLogout ||
                isMemberUpdate ||
                isMemberProfile ||
                isMemberRecommand ||
                isMemberScrap ||
                isMemberProfilePage ||
                isGetScrapCafes ||
                isMemberEditPage;

        return !shouldFilter;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        log.info("Request ID: {} - 요청 URI: {}에 대한 JWT 필터 시작", requestId, request.getRequestURI());

        String jwt = resolveToken(request);
        if (!StringUtils.hasText(jwt)) {
            log.warn("Request ID: {} - JWT 토큰 누락", requestId);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰이 필요합니다.");
            return;
        }

        if (!tokenProvider.validate(jwt)) {
            log.warn("Request ID: {} - 유효하지 않은 JWT 토큰: {}", requestId, jwt);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 JWT 토큰");
            return;
        }

        Authentication authentication = tokenProvider.getAuthentication(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Request ID: {} - 인증 정보 설정: {}", requestId, authentication.getName());

        filterChain.doFilter(request, response);

        long duration = System.currentTimeMillis() - startTime;
        log.info("Request ID: {} - 요청 URI: {}에 대한 JWT 필터 종료. 처리 시간: {} ms",
                requestId, request.getRequestURI(), duration);
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


