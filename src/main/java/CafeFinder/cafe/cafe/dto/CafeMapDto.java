package CafeFinder.cafe.cafe.dto;

import CafeFinder.cafe.global.infrastructure.elasticSearch.IndexedCafe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CafeMapDto {

    private String cafeCode;
    private String name;
    private String address;
    private String openingHours;
    private String phoneNumber;
    private String imageUrl;
    private Double averageRating;
    private String location;

    public static CafeMapDto fromIndexDocument(IndexedCafe indexedCafe) {
        return CafeMapDto.builder()
                .cafeCode(indexedCafe.getCafeCode())
                .name(indexedCafe.getName())
                .address(indexedCafe.getAddress())
                .openingHours(indexedCafe.getOpeningHours())
                .phoneNumber(indexedCafe.getPhoneNumber())
                .imageUrl(indexedCafe.getImageUrl())
                .averageRating(indexedCafe.getAverageRating())
                .location(geoPointToString(indexedCafe.getLocation()))
                .build();
    }

    private static String geoPointToString(Point point) {
        if (point == null) {
            return null;
        }
        return point.getY() + "," + point.getX();
    }

}
