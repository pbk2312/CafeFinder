package CafeFinder.cafe.infrastructure.auth.AuthProvider;


import CafeFinder.cafe.domain.AuthProvider;
import CafeFinder.cafe.infrastructure.auth.OAuth2Member;
import java.util.Map;

public class NaverOAuth2MemberProvider implements OAuth2MemberProvider {

    @Override
    public String getProviderName() {
        return "naver";
    }

    @Override
    public OAuth2Member getUserInfo(Map<String, Object> attributes) {
        Map<String, Object> response = getResponse(attributes);

        return OAuth2Member.builder()
                .provider(AuthProvider.NAVER)
                .id("naver_" + (String) response.get("id"))
                .password((String) response.get("id"))
                .nickname((String) response.get("name") + "_naver")
                .email((String) response.get("email"))
                .build();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getResponse(Map<String, Object> attributes) {
        return (Map<String, Object>) attributes.get("response");
    }

}
