package CafeFinder.cafe.dto;

import CafeFinder.cafe.domain.Cafe;
import CafeFinder.cafe.domain.CafeTheme;
import CafeFinder.cafe.domain.SeoulDistrict;
import CafeFinder.cafe.infrastructure.elasticSearch.IndexedCafe;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CafeDto {

    private String cafeCode;
    private String name;
    private String address;
    private SeoulDistrict district;
    private String openingHours;
    private String phoneNumber;
    private String imageUrl;
    private Double averageRating;
    private Set<CafeTheme> themes;
    private List<CafeReviewDto> reviews;
    private int reviewCount;

    private String location;

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
                .themes(document.getThemes().stream()
                        .map(theme -> CafeTheme.valueOf(theme.toUpperCase()))
                        .collect(Collectors.toSet()))
                .reviewCount(document.getReviewCount() != null ? document.getReviewCount() : 0)
                .location(geoPointToString(document.getLocation()))
                .build();
    }

    public static CafeDto fromEntityWithReviews(Cafe cafeInfo, List<CafeReviewDto> reviewDtos) {
        return CafeDto.builder()
                .cafeCode(cafeInfo.getCode())
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

    private static String geoPointToString(Point point) {
        if (point == null) {
            return null;
        }
        return point.getY() + "," + point.getX();
    }

}
