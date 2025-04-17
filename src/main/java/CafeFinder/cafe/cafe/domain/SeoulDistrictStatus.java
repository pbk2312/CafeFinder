package CafeFinder.cafe.cafe.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SeoulDistrictStatus {

    @Id
    @Column(name = "gu_code")
    private String code;

    @Column(name = "average_rating")
    private double averageRating;

    private int totalReviews;

    public static SeoulDistrictStatus create(String guCode, double averageRating, int totalReviews) {
        return SeoulDistrictStatus.builder()
                .code(guCode)
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .build();
    }

}
