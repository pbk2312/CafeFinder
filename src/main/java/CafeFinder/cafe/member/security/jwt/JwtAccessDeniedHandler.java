package CafeFinder.cafe.member.security.jwt;


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
        String encodedMessage = URLEncoder.encode("권한이 없습니다.",
            StandardCharsets.UTF_8);
        response.sendRedirect("/error?message=" + encodedMessage);
    }

}
