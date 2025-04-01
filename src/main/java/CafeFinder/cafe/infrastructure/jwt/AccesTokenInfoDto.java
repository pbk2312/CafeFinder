package CafeFinder.cafe.infrastructure.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccesTokenInfoDto {

    private String grantType;
    private String accessToken;
    private int accessTokenExpiresIn;

}
