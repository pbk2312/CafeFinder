package CafeFinder.cafe.dto;

import CafeFinder.cafe.domain.SeoulDistrictStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeoulDistrictStatusDto {

    private String guCode;      // 구 코드
    private double averageRating;  // 평균 평점
    private int totalReviews;   // 총 리뷰 수

    // 엔티티 -> DTO
    public static SeoulDistrictStatusDto fromEntity(SeoulDistrictStatus entity) {
        return SeoulDistrictStatusDto.builder()
                .guCode(entity.getCode())
                .averageRating(entity.getAverageRating())
                .totalReviews(entity.getTotalReviews())
                .build();
    }

}
