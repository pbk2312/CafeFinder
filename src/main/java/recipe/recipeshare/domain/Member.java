package recipe.recipeshare.domain;


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
import recipe.recipeshare.dto.MemberSignUpDto;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nickName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    public static Member create(MemberSignUpDto signUpDto, String encodedPassword) {
        return Member.builder()
                .email(signUpDto.getEmail())
                .memberRole(MemberRole.REGULAR)
                .nickName(signUpDto.getNickName())
                .password(encodedPassword)
                .build();
        
    }

}
