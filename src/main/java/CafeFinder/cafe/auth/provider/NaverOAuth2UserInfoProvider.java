package CafeFinder.cafe.auth.provider;


import CafeFinder.cafe.auth.OAuth2UserInfo;
import CafeFinder.cafe.domain.Provider;
import java.util.Map;

public class NaverOAuth2UserInfoProvider implements OAuth2UserInfoProvider {

    @Override
    public String getProviderName() {
        return "naver";
    }

    @Override
    public OAuth2UserInfo getUserInfo(Map<String, Object> attributes) {
        Map<String, Object> response = getResponse(attributes);

        return OAuth2UserInfo.builder()
                .provider(Provider.NAVER)
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
