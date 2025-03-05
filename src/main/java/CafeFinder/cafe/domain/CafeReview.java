package CafeFinder.cafe.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CafeReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 리뷰 고유 ID

    @ManyToOne
    @JoinColumn(name = "cafe_id", nullable = false)
    private CafeInfo cafe;  // CafeInfo 엔티티와 연결 (FK)

    private double rating;  // 별점

    @Column(columnDefinition = "LONGTEXT") // 긴 텍스트 저장 가능
    private String review;  // 리뷰 내용

    public static CafeReview create(CafeInfo cafe, double rating, String review) {
        return CafeReview.builder()
                .cafe(cafe)
                .rating(rating)
                .review(review)
                .build();
    }

}
