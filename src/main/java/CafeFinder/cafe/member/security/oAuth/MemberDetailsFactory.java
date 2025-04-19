package CafeFinder.cafe.member.security.oAuth;


import CafeFinder.cafe.member.exception.UnavailableProviderException;
import java.util.Map;
import org.springframework.security.oauth2.core.user.OAuth2User;

@SuppressWarnings("all")
public class MemberDetailsFactory {

    public static MemberDetails memberDetails(String provider, OAuth2User oAuth2User) {

        Map<String, Object> attributes = oAuth2User.getAttributes();

        switch (provider.toUpperCase().trim()) {
            case "GOOGLE" -> {
                return MemberDetails.builder()
                        .nickName(attributes.get("name").toString())
                        .email(attributes.get("email").toString())
                        .attributes(attributes)
                        .build();
            }
            case "NAVER" -> {
                Map<String, String> properties = (Map<String, String>) attributes.get("response");
                return MemberDetails.builder()
                        .nickName(properties.get("name"))
                        .email(properties.get("id") + "@naver.com")
                        .attributes(attributes)
                        .build();
            }
            case "KAKAO" -> {
                Map<String, String> properties = (Map<String, String>) attributes.get("properties");
                return MemberDetails.builder()
                        .nickName(properties.get("nickname"))
                        .email(attributes.get("id").toString() + "@kakao.com")
                        .build();
            }
            default -> throw new UnavailableProviderException(provider);
        }

    }

}
