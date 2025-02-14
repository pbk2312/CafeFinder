package CafeFinder.cafe.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class TokenDto {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private int accessTokenExpiresIn;

}
