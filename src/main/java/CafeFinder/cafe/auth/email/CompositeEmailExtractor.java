package CafeFinder.cafe.auth.email;

import CafeFinder.cafe.exception.MemberNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;


@AllArgsConstructor
public class CompositeEmailExtractor implements EmailExtractor {

    private final List<EmailExtractor> extractors;

    @Override
    public String extractEmail(OAuth2User oAuth2User) {
        for (EmailExtractor extractor : extractors) {
            String email = extractor.extractEmail(oAuth2User);
            if (email != null) {
                return email;
            }
        }
        throw new MemberNotFoundException();
    }

}
