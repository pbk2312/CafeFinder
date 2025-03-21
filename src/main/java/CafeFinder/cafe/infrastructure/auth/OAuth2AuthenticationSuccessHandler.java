package CafeFinder.cafe.infrastructure.auth;

import CafeFinder.cafe.infrastructure.auth.email.CompositeEmailExtractor;
import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.infrastructure.jwt.TokenDto;
import CafeFinder.cafe.infrastructure.jwt.TokenProvider;
import CafeFinder.cafe.service.interfaces.MemberService;
import CafeFinder.cafe.service.redis.RefreshTokenService;
import CafeFinder.cafe.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Log4j2
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final CompositeEmailExtractor extractor;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("OAuth2 로그인 성공: {}", authentication.getPrincipal());

        // 1. OAuth2User 정보 가져오기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 2. 이메일 추출
        String email = extractor.extractEmail(oAuth2User);

        // 3. Member 조회 또는 생성
        Member member = memberService.getMemberByEmail(email);

        // 4. UsernamePasswordAuthenticationToken 생성
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                member.getEmail(),
                member.getPassword(),
                authentication.getAuthorities()
        );

        // 5. JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(newAuthentication);

        // 6. Refresh Token Redis에 저장
        refreshTokenService.saveRefreshToken(member.getId(), tokenDto.getRefreshToken(),
                tokenDto.getRefreshTokenExpiresIn());

        // 7. Access Token과 Refresh Token을 쿠키에 저장
        CookieUtils.addCookie(response, "accessToken", tokenDto.getAccessToken(), tokenDto.getAccessTokenExpiresIn());

        // 8. 홈 페이지로 리다이렉트 설정
        String targetUrl = "/";  // 홈 페이지 URL
        log.info("리다이렉트 URL: {}", targetUrl);

        // 9. 리다이렉트 처리
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
