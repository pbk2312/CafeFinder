package CafeFinder.cafe.cafe.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Cafe {

    @Id
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 100)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SeoulDistrict district;

    @Column(name = "latitude", columnDefinition = "DECIMAL(10,6)")
    private Double latitude;

    @Column(name = "longitude", columnDefinition = "DECIMAL(10,6)")
    private Double longitude;

    @Column(name = "opening_hours", length = 20)
    private String openingHours;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;
    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "average_rating")
    private Double averageRating;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cafe_themes", joinColumns = @JoinColumn(name = "cafe_code"))
    @Column(name = "theme")
    @Enumerated(EnumType.STRING)
    private Set<CafeTheme> themes;

    @Builder.Default
    @OneToMany(mappedBy = "cafe", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CafeReview> reviews = new ArrayList<>();

    public static Cafe create(String cafeCode, String name, String address, SeoulDistrict district,
                              String openingHours, String phoneNumber, String imageUrl, Double averageRating,
                              Set<CafeTheme> themes, Double latitude, Double longitude) {
        return Cafe.builder()
                .code(cafeCode)
                .name(name)
                .address(address)
                .district(district)
                .openingHours(openingHours)
                .phoneNumber(phoneNumber)
                .imageUrl(imageUrl)
                .averageRating(averageRating)
                .themes(themes)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

}
