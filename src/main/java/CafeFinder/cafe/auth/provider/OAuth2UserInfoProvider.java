package CafeFinder.cafe.auth.provider;

import CafeFinder.cafe.auth.OAuth2UserInfo;
import java.util.Map;

public interface OAuth2UserInfoProvider {
    String getProviderName();

    OAuth2UserInfo getUserInfo(Map<String, Object> attributes);
}
