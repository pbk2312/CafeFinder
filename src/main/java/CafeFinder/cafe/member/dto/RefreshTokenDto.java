package CafeFinder.cafe.member.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RefreshTokenDto {
    private String refreshToken;
}
