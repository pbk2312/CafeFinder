package CafeFinder.cafe.infrastructure.auth.email;

import org.springframework.security.oauth2.core.user.OAuth2User;

public class GoogleEmailExtractor implements EmailExtractor {
    
    @Override
    public String extractEmail(OAuth2User oAuth2User) {
        return oAuth2User.getAttribute("email");
    }

}
