package CafeFinder.cafe.cafe.dto;


import CafeFinder.cafe.cafe.domain.CafeReview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CafeReviewDto {

    private double rating;

    private String review;
    
    public static CafeReviewDto fromEntity(CafeReview entity) {
        return CafeReviewDto.builder()
                .review(entity.getContent())
                .rating(entity.getRating())
                .build();
    }

}
