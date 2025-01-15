package recipe.recipeshare.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String LOGIN_ERROR_MESSAGE = "로그인이 필요한 서비스입니다.";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String redirectUrl = "/login";

        String encodedMessage = URLEncoder.encode(LOGIN_ERROR_MESSAGE, StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl + "?errorMessage=" + encodedMessage);
    }

}
