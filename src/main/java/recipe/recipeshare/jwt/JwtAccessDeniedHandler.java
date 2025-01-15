package recipe.recipeshare.jwt;

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

    private static final String MESSAGE = "고객님의 등급은 접근할수없습니다.";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        String encodedMessage = URLEncoder.encode(MESSAGE, StandardCharsets.UTF_8);
        response.sendRedirect("/error?message=" + encodedMessage);
    }

}
