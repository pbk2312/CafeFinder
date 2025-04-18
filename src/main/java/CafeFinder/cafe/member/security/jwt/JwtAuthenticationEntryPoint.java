package CafeFinder.cafe.member.security.jwt;

import static CafeFinder.cafe.global.util.ErrorMessage.LOGIN_ERROR_MESSAGE;

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

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String redirectUrl = "/member/signupAndLogin";

        String encodedMessage = URLEncoder.encode(LOGIN_ERROR_MESSAGE.getMessage(),
                StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl + "?errorMessage=" + encodedMessage);
    }

}
