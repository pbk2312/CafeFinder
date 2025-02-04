package recipe.recipeshare.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    private String recipeId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<FoodType> foodTypes;

    @ElementCollection
    private List<String> ingredients;

    @Column
    private Double rating;

    @Column
    private Integer cookingTime; // 조리 시간 (분 단위)

    @Column
    private String difficulty; // 난이도 (예: 초급, 중급, 고급)

    @Column
    private String imageUrl; // 레시피 이미지 URL

    // 회원과 양방향 관 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member author;

}
