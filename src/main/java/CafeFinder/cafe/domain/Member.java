package CafeFinder.cafe.domain;

import CafeFinder.cafe.dto.MemberSignUpDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "nick_name", nullable = false, unique = true, length = 20)
    private String nickName;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AuthProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", length = 20)
    private MemberRole memberRole;

    @Column(name = "profile_image_path", length = 255)
    private String profileImagePath;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CafeScrap> scraps = new ArrayList<>();

    public static Member create(MemberSignUpDto signUpDto, String encodedPassword, String profileImagePath) {
        return Member.builder()
                .email(signUpDto.getEmail())
                .memberRole(MemberRole.REGULAR)
                .provider(AuthProvider.LOCAL)
                .nickName(signUpDto.getNickName())
                .password(encodedPassword)
                .profileImagePath(profileImagePath)
                .build();
    }

    public void updateProfile(String newNickName, String newProfileImagePath) {
        if (newNickName != null && !newNickName.isEmpty()) {
            this.nickName = newNickName;
        }
        if (newProfileImagePath != null && !newProfileImagePath.isEmpty()) {
            this.profileImagePath = newProfileImagePath;
        }
    }

}
