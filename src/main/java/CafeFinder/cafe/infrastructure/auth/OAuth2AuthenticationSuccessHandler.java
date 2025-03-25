package CafeFinder.cafe.infrastructure.auth;

import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.infrastructure.auth.email.CompositeEmailExtractor;
import CafeFinder.cafe.infrastructure.jwt.TokenDto;
import CafeFinder.cafe.infrastructure.jwt.TokenProvider;
import CafeFinder.cafe.service.interfaces.MemberService;
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
    private final CompositeEmailExtractor extractor;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("OAuth2 로그인 성공: {}", authentication.getPrincipal());

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = extractor.extractEmail(oAuth2User);

        Member member = memberService.getMemberByEmail(email);

        Authentication oAuth2Authentication = new UsernamePasswordAuthenticationToken(
                member.getEmail(),
                member.getPassword(),
                authentication.getAuthorities()
        );

        TokenDto tokenDto = tokenProvider.generateTokenDto(oAuth2Authentication);

        CookieUtils.addCookie(response, "accessToken", tokenDto.getAccessToken(), tokenDto.getAccessTokenExpiresIn());
        CookieUtils.addCookie(response, "refreshToken", tokenDto.getRefreshToken(),
                tokenDto.getRefreshTokenExpiresIn());

        String targetUrl = "/";  // 홈 페이지 URL
        log.info("리다이렉트 URL: {}", targetUrl);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }

}
