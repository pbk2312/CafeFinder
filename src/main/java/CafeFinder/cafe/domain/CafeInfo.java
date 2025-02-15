package CafeFinder.cafe.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String cafeCode;  // 카페 ID (MP1, MP2 등)

    @Column(nullable = false, length = 100)
    private String name;  // 카페명

    @Column(nullable = false, length = 255)
    private String address;  // 주소

    @Column(length = 255)
    private String hours;  // 영업시간

    @Column(length = 20)
    private String phone;  // 전화번호

    @Column(columnDefinition = "TEXT")
    private String imageUrl;  // 대표사진 URL

    public static CafeInfo create(String cafeCode, String name, String address, String hours, String phone,
                                  String imageUrl) {
        return CafeInfo.builder()
                .cafeCode(cafeCode)
                .name(name)
                .address(address)
                .hours(hours)
                .phone(phone)
                .imageUrl(imageUrl)
                .build();
    }

}
