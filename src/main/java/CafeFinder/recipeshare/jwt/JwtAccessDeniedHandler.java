package CafeFinder.recipeshare.jwt;

import static CafeFinder.recipeshare.util.ErrorMessage.LEVEL_MESSAGE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        String encodedMessage = URLEncoder.encode(LEVEL_MESSAGE.getMessage(), StandardCharsets.UTF_8);
        response.sendRedirect("/error?message=" + encodedMessage);
    }

}
