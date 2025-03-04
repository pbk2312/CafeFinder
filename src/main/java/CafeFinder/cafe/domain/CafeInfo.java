package CafeFinder.cafe.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
public class CafeInfo {

    @Id
    @Column(nullable = false, unique = true, length = 20)
    private String cafeCode;  // 카페 ID (MP1, MP2 등)

    @Column(nullable = false, length = 100)
    private String name;  // 카페명

    @Column(nullable = false, length = 255)
    private String address;  // 주소

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CafeDistrict district;  // 행정구 (ex: MP -> 마포구)

    @Column(length = 255)
    private String hours;  // 영업시간

    @Column(length = 20)
    private String phone;  // 전화번호

    @Column(columnDefinition = "TEXT")
    private String imageUrl;  // 대표사진 URL

    @Column(length = 20)
    private Double review;  // 평균 평점

    @ElementCollection
    @CollectionTable(name = "cafe_themes", joinColumns = @JoinColumn(name = "cafe_code"))
    @Enumerated(EnumType.STRING)
    private Set<CafeTheme> themes; // 여러 개의 테마

    public void updateReview(Double review) {
        this.review = review;
    }

    public static CafeInfo create(String cafeCode, String name, String address, CafeDistrict district,
                                  String hours, String phone, String imageUrl) {
        return CafeInfo.builder()
                .cafeCode(cafeCode)
                .name(name)
                .address(address)
                .district(district)
                .hours(hours)
                .phone(phone)
                .imageUrl(imageUrl)
                .build();
    }

}
