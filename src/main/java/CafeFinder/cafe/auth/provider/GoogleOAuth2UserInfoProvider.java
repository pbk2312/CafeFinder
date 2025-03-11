package CafeFinder.cafe.auth.provider;

import CafeFinder.cafe.auth.OAuth2UserInfo;
import CafeFinder.cafe.domain.Provider;
import java.util.Map;

public class GoogleOAuth2UserInfoProvider implements OAuth2UserInfoProvider {

    @Override
    public String getProviderName() {
        return "google";
    }

    @Override
    public OAuth2UserInfo getUserInfo(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .provider(Provider.GOOGLE)
                .id("google_" + (String) attributes.get("sub"))
                .password((String) attributes.get("sub")) // 구글은 ID를 password로 사용
                .nickname((String) attributes.get("name") + "_google")
                .email((String) attributes.get("email"))
                .build();
    }
    
}
