package CafeFinder.cafe.service.cafe;

import CafeFinder.cafe.domain.CafeDistrict;
import CafeFinder.cafe.domain.CafeInfo;
import CafeFinder.cafe.domain.CafeTheme;
import CafeFinder.cafe.dto.CafeInfoDto;
import CafeFinder.cafe.dto.CafeReviewDto;
import CafeFinder.cafe.exception.CafeInfoNotFoundException;
import CafeFinder.cafe.exception.WrongDistrictAndTheme;
import CafeFinder.cafe.repository.CafeInfoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class CafeInfoService {

    private final CafeInfoRepository cafeInfoRepository;

    @Transactional
    public void saveCafes(List<CafeInfo> cafes) {
        cafeInfoRepository.saveAll(cafes);
    }

    @Transactional(readOnly = true)
    public long countCafes() {
        return cafeInfoRepository.count();  // DB에 저장된 카페 개수 확인
    }

    // 구별, 테마별 검색 기능
    @Transactional(readOnly = true)
    public Page<CafeInfoDto> getCafesByDistrictAndTheme(String district, String theme, Pageable pageable) {
        try {
            log.info("district : {} , theme : {} ", district, theme);
            CafeDistrict cafeDistrict = CafeDistrict.valueOf(district.toUpperCase());
            CafeTheme cafeTheme = CafeTheme.valueOf(theme.toUpperCase());

            Page<CafeInfo> byDistrictAndTheme = cafeInfoRepository.findByDistrictAndThemesContaining(cafeDistrict,
                    cafeTheme,
                    pageable);
            return byDistrictAndTheme.map(CafeInfoDto::fromEntityForList);
        } catch (IllegalArgumentException e) {
            throw new WrongDistrictAndTheme();
        }
    }

    // 카페 상세 조회
    @Transactional(readOnly = true)
    public CafeInfoDto getCafeInfo(String cafeCode) {
        CafeInfo cafeInfo = cafeInfoRepository.findByCafeCodeWithReviews(cafeCode)
                .orElseThrow(() -> new CafeInfoNotFoundException(cafeCode));

        List<CafeReviewDto> reviewDtos = cafeInfo.getReviews().stream()
                .map(CafeReviewDto::fromEntity)
                .toList();

        return CafeInfoDto.fromEntityWithReviews(cafeInfo, reviewDtos);
    }

}
