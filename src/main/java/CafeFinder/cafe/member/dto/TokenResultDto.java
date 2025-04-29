package CafeFinder.cafe.member.dto;

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
