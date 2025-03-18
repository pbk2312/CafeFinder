package CafeFinder.cafe.domain;


import CafeFinder.cafe.dto.MemberSignUpDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nickName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AuthProvider provider; // 구글, 카카오 , 네이버, 일반

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    private String profileImagePath; // 프로필 이미지 파일 경로

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
