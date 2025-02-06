package recipe.recipeshare.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID recipeId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<FoodType> foodTypes;

    @ElementCollection
    private List<String> ingredients;

    @Column
    private Duration cookingTime; // 조리 시간 (분 단위)

    @Column
    private String imageUrl; // 레시피 이미지 URL

    @Column(nullable = false)
    private Long views = 0L;  // 조회수 (기본값은 0)

    @Column(nullable = false)
    private Double averageRating = 0.0; // 평점 (기본값은 0.0)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member author;

    // Recipe 객체 생성 메서드
    public static Recipe create(String title, String content, Difficulty difficulty, List<FoodType> foodTypes,
                                List<String> ingredients, Duration cookingTime, String imageUrl, Member author) {
        return Recipe.builder()
                .title(title)
                .content(content)
                .difficulty(difficulty)
                .foodTypes(foodTypes)
                .ingredients(ingredients)
                .cookingTime(cookingTime)
                .imageUrl(imageUrl)
                .author(author)
                .views(0L)
                .averageRating(0.0)
                .build();
    }

    // 조회수 증가 메서드
    public void incrementViews() {
        this.views++;
    }

    // 평점 업데이트 메서드
    public void updateRating(double rating) {
        this.averageRating = (this.averageRating + rating) / 2; // 평점의 평균을 계산
    }
    
}
