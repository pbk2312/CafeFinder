package CafeFinder.cafe.dto;

import CafeFinder.cafe.infrastructure.jwt.AccesTokenDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResultDto {
    private boolean isAccessTokenValid;
    private boolean isRefreshTokenValid;
    private AccesTokenDto newAccessToken;
    private String message;
}
