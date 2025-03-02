package CafeFinder.cafe.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CafeTheme {
    COZY("분위기 좋은"),
    QUIET("조용한"),
    STUDY_FRIENDLY("공부하기 좋은"),
    DESSERT("디저트 맛집"),
    SPECIALTY_COFFEE("커피 맛집"),
    NONE("테마 없음");
    private final String description;
}
