package CafeFinder.cafe.dto;


import CafeFinder.cafe.domain.CafeReview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CafeReviewDto {

    private double rating;  // 별점

    private String review;  // 리뷰 내용

    public static CafeReviewDto fromEntity(CafeReview entity) {
        return CafeReviewDto.builder()
                .review(entity.getReview())
                .rating(entity.getRating())
                .build();
    }

}
