package CafeFinder.cafe.dto;

import CafeFinder.cafe.domain.CafeTheme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ThemeDto {
    private String name;
    private String description;

    public static ThemeDto fromEntity(CafeTheme cafeTheme) {
        return ThemeDto.builder()
                .name(cafeTheme.name())
                .description(cafeTheme.getDescription())
                .build();
    }
    
}
