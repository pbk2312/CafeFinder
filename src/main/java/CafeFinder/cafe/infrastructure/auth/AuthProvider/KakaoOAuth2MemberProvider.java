package CafeFinder.cafe.infrastructure.auth.AuthProvider;

import CafeFinder.cafe.domain.AuthProvider;
import CafeFinder.cafe.infrastructure.auth.OAuth2Member;
import java.util.Map;

public class KakaoOAuth2MemberProvider implements OAuth2MemberProvider {

    @Override
    public String getProviderName() {
        return "kakao";
    }

    @Override
    public OAuth2Member getUserInfo(Map<String, Object> attributes) {
        Map<String, Object> properties = getValue(attributes, "properties");
        Map<String, Object> kakaoAccount = getValue(attributes, "kakao_account");

        String email = (String) kakaoAccount.get("email");
        return OAuth2Member.builder()
                .provider(AuthProvider.KAKAO)
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
