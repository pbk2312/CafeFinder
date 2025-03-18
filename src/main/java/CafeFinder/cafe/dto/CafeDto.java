package CafeFinder.cafe.dto;

import CafeFinder.cafe.domain.Cafe;
import CafeFinder.cafe.domain.CafeTheme;
import CafeFinder.cafe.domain.SeoulDistrict;
import CafeFinder.cafe.infrastructure.elasticSearch.IndexedCafe;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CafeDto {

    private String cafeCode;  // 카페 ID (MP1, MP2 등)
    private String name;  // 카페명
    private String address;  // 주소
    private SeoulDistrict district; // 행정구 (ex: MP -> 마포구)
    private String openingHours;  // 영업시간
    private String phoneNumber;  // 전화번호
    private String imageUrl;  // 대표사진 URL
    private Double averageRating;  // 평균 평점
    private Set<CafeTheme> themes;

    private List<CafeReviewDto> reviews; // 리뷰 목록
    private int reviewCount;

    // Elasticsearch Document -> DTO 변환 메서드 (목록용)
    public static CafeDto fromDocumentForList(IndexedCafe document) {
        return CafeDto.builder()
                .cafeCode(document.getCafeCode())
                .name(document.getName())
                .address(document.getAddress())
                .district(SeoulDistrict.valueOf(document.getDistrict().toUpperCase()))
                .openingHours(document.getOpeningHours())
                .phoneNumber(document.getPhoneNumber())
                .imageUrl(document.getImageUrl())
                .averageRating(document.getAverageRating())
                .themes(Set.of(document.getThemes().stream()
                        .map(theme -> CafeTheme.valueOf(theme.toUpperCase()))
                        .toArray(CafeTheme[]::new)))
                .reviewCount(document.getReviewCount() != null ? document.getReviewCount() : 0)
                .build();
    }

    // 상세 조회용 변환 메서드 (리뷰 포함)
    public static CafeDto fromEntityWithReviews(Cafe cafeInfo, List<CafeReviewDto> reviewDtos) {
        return CafeDto.builder()
                .cafeCode(cafeInfo.getCafeCode())
                .name(cafeInfo.getName())
                .address(cafeInfo.getAddress())
                .district(cafeInfo.getDistrict())
                .openingHours(cafeInfo.getOpeningHours())
                .phoneNumber(cafeInfo.getPhoneNumber())
                .imageUrl(cafeInfo.getImageUrl())
                .averageRating(cafeInfo.getAverageRating())
                .themes(cafeInfo.getThemes())
                .reviews(reviewDtos)
                .reviewCount(reviewDtos.size())
                .build();
    }

}
