package CafeFinder.cafe.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CafeScrapDto {
    private String accessToken;
    private String cafeCode;
}
