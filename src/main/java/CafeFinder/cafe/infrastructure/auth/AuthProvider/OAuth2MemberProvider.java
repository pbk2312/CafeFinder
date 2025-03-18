package CafeFinder.cafe.infrastructure.auth.AuthProvider;

import CafeFinder.cafe.infrastructure.auth.OAuth2Member;
import java.util.Map;

public interface OAuth2MemberProvider {
    String getProviderName();

    OAuth2Member getUserInfo(Map<String, Object> attributes);
}
