package CafeFinder.cafe.infrastructure.auth.AuthProvider;


import CafeFinder.cafe.exception.UnsupportedProviderException;
import CafeFinder.cafe.infrastructure.auth.OAuth2Member;
import java.util.HashMap;
import java.util.Map;

public class OAuth2MemberFactory {

    private static final Map<String, OAuth2MemberProvider> providers = new HashMap<>();

    static {
        // 각 프로바이더 구현체를 등록합니다.
        OAuth2MemberProvider googleProvider = new GoogleOAuth2MemberProvider();
        OAuth2MemberProvider kakaoProvider = new KakaoOAuth2MemberProvider();
        OAuth2MemberProvider naverProvider = new NaverOAuth2MemberProvider();

        providers.put(googleProvider.getProviderName(), googleProvider);
        providers.put(kakaoProvider.getProviderName(), kakaoProvider);
        providers.put(naverProvider.getProviderName(), naverProvider);
    }

    public static OAuth2Member getOAuth2UserInfo(String provider, Map<String, Object> attributes) {
        OAuth2MemberProvider infoProvider = providers.get(provider.toLowerCase());
        if (infoProvider == null) {
            throw new UnsupportedProviderException(provider);
        }
        return infoProvider.getUserInfo(attributes);
    }

}
