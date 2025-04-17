package CafeFinder.cafe.cafe.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScrapCafeCodeDto {
    private String cafeCode;

    public static ScrapCafeCodeDto from(String cafeCode) {
        return ScrapCafeCodeDto.builder().cafeCode(cafeCode).build();
    }

}
