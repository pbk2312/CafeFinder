package CafeFinder.cafe.member.oAuth;

import CafeFinder.cafe.member.dto.TokenDto;
import CafeFinder.cafe.member.jwt.TokenProvider;
import CafeFinder.cafe.member.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        Authentication oAuth2Authentication = new UsernamePasswordAuthenticationToken(
                memberDetails.getEmail(),
                null,
                authentication.getAuthorities()
        );

        TokenDto tokenDto = tokenProvider.generateTokenDto(oAuth2Authentication);

        CookieUtils.addCookie(response, "accessToken", tokenDto.getAccessToken(),
                tokenDto.getAccessTokenExpiresIn());
        CookieUtils.addCookie(response, "refreshToken", tokenDto.getRefreshToken(),
                tokenDto.getRefreshTokenExpiresIn());

        String targetUrl = "/";

        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }

}
