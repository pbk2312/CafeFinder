package CafeFinder.cafe.infrastructure.auth.email;

import java.util.Map;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class NaverEmailExtractor implements EmailExtractor {

    @Override
    public String extractEmail(OAuth2User oAuth2User) {
        Map<String, Object> naverResponse = oAuth2User.getAttribute("response");
        return naverResponse != null ? (String) naverResponse.get("email") : null;
    }
    
}
