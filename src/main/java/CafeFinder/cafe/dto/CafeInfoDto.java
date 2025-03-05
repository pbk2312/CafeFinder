package CafeFinder.cafe.dto;

import CafeFinder.cafe.domain.CafeDistrict;
import CafeFinder.cafe.domain.CafeInfo;
import CafeFinder.cafe.domain.CafeTheme;
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
public class CafeInfoDto {

    private String cafeCode;  // 카페 ID (MP1, MP2 등)
    private String name;  // 카페명
    private String address;  // 주소
    private CafeDistrict district; // 행정구 (ex: MP -> 마포구)
    private String hours;  // 영업시간
    private String phone;  // 전화번호
    private String imageUrl;  // 대표사진 URL
    private Double review;  // 평균 평점
    private Set<CafeTheme> themes; // 여러 개의 카페 테마 저장

    // 목록 조회 때 사용 X
    private List<CafeReviewDto> reviews;


    // 엔티티 -> DTO
    public static CafeInfoDto fromEntityForList(CafeInfo cafeInfo) {
        return CafeInfoDto.builder()
                .cafeCode(cafeInfo.getCafeCode())
                .name(cafeInfo.getName())
                .address(cafeInfo.getAddress())
                .district(cafeInfo.getDistrict())
                .hours(cafeInfo.getHours())
                .phone(cafeInfo.getPhone())
                .imageUrl(cafeInfo.getImageUrl())
                .review(cafeInfo.getReview())
                .themes(cafeInfo.getThemes())
                .build();
    }

    // 상세 조회용 변환 메서드 (리뷰 목록 포함)
    public static CafeInfoDto fromEntityWithReviews(CafeInfo cafeInfo, List<CafeReviewDto> reviewDtos) {
        return CafeInfoDto.builder()
                .cafeCode(cafeInfo.getCafeCode())
                .name(cafeInfo.getName())
                .address(cafeInfo.getAddress())
                .district(cafeInfo.getDistrict())
                .hours(cafeInfo.getHours())
                .phone(cafeInfo.getPhone())
                .imageUrl(cafeInfo.getImageUrl())
                .review(cafeInfo.getReview())
                .themes(cafeInfo.getThemes())
                .reviews(reviewDtos)
                .build();
    }

}
