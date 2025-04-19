package CafeFinder.cafe.member.security.oAuth;

import CafeFinder.cafe.member.domain.AuthProvider;
import CafeFinder.cafe.member.domain.Member;
import CafeFinder.cafe.member.domain.MemberRole;
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

