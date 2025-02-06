package recipe.recipeshare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import recipe.recipeshare.domain.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}
