package CafeFinder.cafe.infrastructure.auth.AuthProvider;

import CafeFinder.cafe.domain.AuthProvider;
import CafeFinder.cafe.infrastructure.auth.OAuth2Member;
import java.util.Map;

public class GoogleOAuth2MemberProvider implements OAuth2MemberProvider {

    @Override
    public String getProviderName() {
        return "google";
    }

    @Override
    public OAuth2Member getUserInfo(Map<String, Object> attributes) {
        return OAuth2Member.builder()
                .provider(AuthProvider.GOOGLE)
                .id("google_" + (String) attributes.get("sub"))
                .password((String) attributes.get("sub")) // 구글은 ID를 password로 사용
                .nickname((String) attributes.get("name") + "_google")
                .email((String) attributes.get("email"))
                .build();
    }

}
