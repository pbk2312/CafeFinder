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
    private Double latitude;
    private Double longitude;

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
                .themes(
                        document.getThemes() == null
                                ? null
                                : document.getThemes().stream()
                                        .map(theme -> CafeTheme.valueOf(theme.toUpperCase()))
                                        .collect(Collectors.toSet())
                )
                .reviewCount(document.getReviewCount() != null ? document.getReviewCount() : 0)
                .latitude(document.getLocation().getX())
                .longitude(document.getLocation().getY())
                .build();
    }

    public static CafeDto fromEntity(Cafe cafe) {
        return CafeDto.builder()
                .cafeCode(cafe.getCode())
                .name(cafe.getName())
                .address(cafe.getAddress())
                .district(cafe.getDistrict())
                .openingHours(cafe.getOpeningHours())
                .phoneNumber(cafe.getPhoneNumber())
                .imageUrl(cafe.getImageUrl())
                .averageRating(cafe.getAverageRating())
                .themes(cafe.getThemes())
                .latitude(cafe.getLatitude())
                .longitude(cafe.getLongitude())
                .build();
    }


    public Cafe toEntity() {
        return Cafe.create(
                this.cafeCode,
                this.name,
                this.address,
                this.district,
                this.openingHours,
                this.phoneNumber,
                this.imageUrl,
                this.averageRating,
                this.themes,
                this.latitude,
                this.longitude
        );
    }

}
