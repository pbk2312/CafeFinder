package CafeFinder.cafe.infrastructure.auth.email;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface EmailExtractor {

    String extractEmail(OAuth2User oAuth2User);

}
