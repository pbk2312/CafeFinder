package CafeFinder.cafe.auth.provider;

import CafeFinder.cafe.auth.OAuth2UserInfo;
import CafeFinder.cafe.domain.Provider;
import java.util.Map;

public class KakaoOAuth2UserInfoProvider implements OAuth2UserInfoProvider {

    @Override
    public String getProviderName() {
        return "kakao";
    }

    @Override
    public OAuth2UserInfo getUserInfo(Map<String, Object> attributes) {
        Map<String, Object> properties = getValue(attributes, "properties");
        Map<String, Object> kakaoAccount = getValue(attributes, "kakao_account");

        String email = (String) kakaoAccount.get("email");
        return OAuth2UserInfo.builder()
                .provider(Provider.KAKAO)
                .id("kakao_" + attributes.get("id").toString())
                .password(attributes.get("id").toString())
                .nickname((String) properties.get("nickname") + "_kakao")
                .email(email)
                .build();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getValue(Map<String, Object> attributes, String properties) {
        return (Map<String, Object>) attributes.get(properties);
    }

}
