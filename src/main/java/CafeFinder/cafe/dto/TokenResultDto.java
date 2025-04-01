package CafeFinder.cafe.dto;

import CafeFinder.cafe.infrastructure.jwt.AccesTokenInfoDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResultDto {
    private boolean isAccessTokenValid;
    private boolean isRefreshTokenValid;
    private AccesTokenInfoDto newAccessToken;
    private String message;
}
