package CafeFinder.cafe.auth;

import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.domain.MemberRole;
import CafeFinder.cafe.domain.Provider;
import CafeFinder.cafe.exception.UnsupportedProviderException;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;

@Builder
@Getter
@ToString
@SuppressWarnings("unchecked")
public class OAuth2UserInfo {

    private String id;
    private String password;
    private String email;
    private String nickname;
    private Provider provider;

    @Value("${file.default.image}")
    private String DEFAULT_PROFILE_IMAG;

    public static OAuth2UserInfo of(String provider, Map<String, Object> attributes) {
        return switch (provider) {
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            case "naver" -> ofNaver(attributes);
            default -> throw new UnsupportedProviderException(provider);
        };
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .provider(Provider.GOOGLE)
                .id("google_" + (String) attributes.get("sub"))
                .password((String) attributes.get("sub")) // 구글은 ID를 password로 사용
                .nickname((String) attributes.get("name") + "_google") // 닉네임에 provider 추가
                .email((String) attributes.get("email"))
                .build();
    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        String email = (String) kakaoAccount.get("email");
        return OAuth2UserInfo.builder()
                .provider(Provider.KAKAO)
                .id("kakao_" + attributes.get("id").toString())
                .password(attributes.get("id").toString())
                .nickname((String) properties.get("nickname") + "_kakao") // 닉네임에 provider 추가
                .email(email)
                .build();
    }

    private static OAuth2UserInfo ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2UserInfo.builder()
                .provider(Provider.NAVER)
                .id("naver_" + (String) response.get("id"))
                .password((String) response.get("id")) // 네이버는 ID를 password로 사용
                .nickname((String) response.get("name") + "_naver") // 닉네임에 provider 추가
                .email((String) response.get("email"))
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .profileImagePath(DEFAULT_PROFILE_IMAG)
                .password(password) // 소셜 로그인에서 제공하는 ID를 password로 사용
                .provider(provider)
                .nickName(nickname)
                .memberRole(MemberRole.REGULAR) // 기본 역할로 설정
                .build();
    }

}
