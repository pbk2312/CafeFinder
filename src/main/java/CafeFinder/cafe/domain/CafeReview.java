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
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cafe_code", nullable = false)
    private Cafe cafe;

    @Column(nullable = false)
    private double rating;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    public static CafeReview create(Cafe cafe, double rating, String content) {
        return CafeReview.builder()
                .cafe(cafe)
                .rating(rating)
                .content(content)
                .build();
    }

}
