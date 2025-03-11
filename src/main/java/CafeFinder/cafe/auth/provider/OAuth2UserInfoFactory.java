package CafeFinder.cafe.auth.provider;


import CafeFinder.cafe.auth.OAuth2UserInfo;
import CafeFinder.cafe.exception.UnsupportedProviderException;
import java.util.HashMap;
import java.util.Map;

public class OAuth2UserInfoFactory {

    private static final Map<String, OAuth2UserInfoProvider> providers = new HashMap<>();

    static {
        // 각 프로바이더 구현체를 등록합니다.
        OAuth2UserInfoProvider googleProvider = new GoogleOAuth2UserInfoProvider();
        OAuth2UserInfoProvider kakaoProvider = new KakaoOAuth2UserInfoProvider();
        OAuth2UserInfoProvider naverProvider = new NaverOAuth2UserInfoProvider();

        providers.put(googleProvider.getProviderName(), googleProvider);
        providers.put(kakaoProvider.getProviderName(), kakaoProvider);
        providers.put(naverProvider.getProviderName(), naverProvider);
    }

    public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {
        OAuth2UserInfoProvider infoProvider = providers.get(provider.toLowerCase());
        if (infoProvider == null) {
            throw new UnsupportedProviderException(provider);
        }
        return infoProvider.getUserInfo(attributes);
    }

}
