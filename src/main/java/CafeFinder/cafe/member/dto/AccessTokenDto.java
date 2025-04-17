package CafeFinder.cafe.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccessTokenDto {
    private String accessToken;
}
