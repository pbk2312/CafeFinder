package CafeFinder.cafe.infrastructure.auth.email;

import java.util.Map;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class KakaoEmailExtractor implements EmailExtractor {

    @Override
    public String extractEmail(OAuth2User oAuth2User) {
        Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
        return kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
    }
    
}
