package CafeFinder.cafe.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeoulDistrictStatus {

    @Id
    @Column(name = "gu_code", length = 10, nullable = false)
    private String guCode;  // 구 코드

    @Column(name = "average_rating", nullable = false)
    private double averageRating;  // 평균 평점

    @Column(name = "total_reviews", nullable = false)
    private int totalReviews;  // 총 리뷰 수

    public static SeoulDistrictStatus create(String guCode, double averageRating, int totalReviews) {
        return SeoulDistrictStatus.builder()
                .guCode(guCode)
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .build();
    }

}
