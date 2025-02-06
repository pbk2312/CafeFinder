package recipe.recipeshare.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Duration;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import recipe.recipeshare.domain.Difficulty;
import recipe.recipeshare.domain.FoodType;

@Getter
@NoArgsConstructor
public class RecipeRequestDto {

    @NotBlank(message = "레시피 제목은 필수 값입니다.")
    private String title;

    @NotBlank(message = "레시피 내용은 필수 값입니다.")
    @Size(max = 1000, message = "레시피 내용은 최대 1000자까지 가능합니다.")
    private String content;

    @NotNull(message = "난이도는 필수 값입니다.")
    private Difficulty difficulty;

    @NotEmpty(message = "음식 종류를 최소 하나 이상 선택해야 합니다.")
    private List<FoodType> foodTypes;

    @NotEmpty(message = "재료 목록을 최소 하나 이상 입력해야 합니다.")
    private List<String> ingredients;

    @Positive(message = "조리 시간은 1분 이상이어야 합니다.")
    private Duration cookingTime;

    private String imageUrl;

    @Builder
    public RecipeRequestDto(String title, String content, Difficulty difficulty,
                            List<FoodType> foodTypes, List<String> ingredients,
                            Duration cookingTime, String imageUrl) {
        this.title = title;
        this.content = content;
        this.difficulty = difficulty;
        this.foodTypes = foodTypes;
        this.ingredients = ingredients;
        this.cookingTime = cookingTime;
        this.imageUrl = imageUrl;
    }

}
