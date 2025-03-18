package CafeFinder.cafe.domain;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cafe {

    @Id
    @Column(nullable = false, unique = true, length = 20)
    private String cafeCode;  // 카페 ID (MP1, MP2 등)

    @Column(nullable = false, length = 100)
    private String name;  // 카페명

    @Column(nullable = false)
    private String address;  // 주소

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SeoulDistrict district;  // 행정구 (ex: MP -> 마포구)

    private String openingHours;  // 영업시간

    @Column(length = 20)
    private String phoneNumber;  // 전화번호

    @Column(columnDefinition = "TEXT")
    private String imageUrl;  // 대표사진 URL

    @Column(length = 20)
    private Double averageRating;  // 평균 평점

    @ElementCollection(fetch = FetchType.EAGER) // Elasticsearch 저장 시 JSON에 포함되도록
    @CollectionTable(name = "cafe_themes", joinColumns = @JoinColumn(name = "cafe_code"))
    @Enumerated(EnumType.STRING)
    private Set<CafeTheme> themes; // 여러 개의 테마

    @OneToMany(mappedBy = "cafe")
    private List<CafeReview> reviews = new ArrayList<>();


    public void updateReview(Double review) {
        this.averageRating = review;
    }

    public static Cafe create(String cafeCode, String name, String address, SeoulDistrict district,
                              String openingHours, String phoneNumber, String imageUrl, Double averageRating,
                              Set<CafeTheme> themes) {
        return Cafe.builder()
                .cafeCode(cafeCode)
                .name(name)
                .address(address)
                .district(district)
                .openingHours(openingHours)
                .phoneNumber(phoneNumber)
                .imageUrl(imageUrl)
                .averageRating(averageRating)
                .themes(themes)
                .build();
    }

}
