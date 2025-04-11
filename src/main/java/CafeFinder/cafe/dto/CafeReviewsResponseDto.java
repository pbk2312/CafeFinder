package CafeFinder.cafe.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CafeReviewsResponseDto {
    private List<CafeReviewDto> reviews;
    private int reviewCount;
}
