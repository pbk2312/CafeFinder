package CafeFinder.cafe.infrastructure.auth;

import CafeFinder.cafe.domain.AuthProvider;
import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.domain.MemberRole;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class OAuth2Member {
    private String id;
    private String password;
    private String email;
    private String nickname;
    private AuthProvider provider;

    // 기본 프로필 이미지는 외부에서 주입하도록 변경
    public Member toEntity(String defaultProfileImage) {
        return Member.builder()
                .email(email)
                .profileImagePath(defaultProfileImage)
                .password(password)
                .provider(provider)
                .nickName(nickname)
                .memberRole(MemberRole.REGULAR)
                .build();
    }

}

