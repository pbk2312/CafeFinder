package CafeFinder.cafe.dto;

import CafeFinder.cafe.domain.CafeTheme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CafeThemeDto {
    private String name;
    private String description;

    public static CafeThemeDto fromEntity(CafeTheme cafeTheme) {
        return CafeThemeDto.builder()
                .name(cafeTheme.name())
                .description(cafeTheme.getDescription())
                .build();
    }

}
