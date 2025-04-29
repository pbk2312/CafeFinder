package CafeFinder.cafe.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccesTokenDto {

    private String accessToken;
    private int accessTokenExpiresIn;

}
