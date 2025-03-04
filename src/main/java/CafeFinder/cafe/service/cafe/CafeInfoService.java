package CafeFinder.cafe.service.cafe;

import CafeFinder.cafe.domain.CafeDistrict;
import CafeFinder.cafe.domain.CafeInfo;
import CafeFinder.cafe.domain.CafeTheme;
import CafeFinder.cafe.dto.CafeInfoDto;
import CafeFinder.cafe.exception.WrongDistrictAndTheme;
import CafeFinder.cafe.repository.CafeInfoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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
            CafeDistrict cafeDistrict = CafeDistrict.valueOf(district.toUpperCase());
            CafeTheme cafeTheme = CafeTheme.valueOf(theme.toUpperCase());

            Page<CafeInfo> byDistrictAndTheme = cafeInfoRepository.findByDistrictAndThemesContaining(cafeDistrict,
                    cafeTheme,
                    pageable);
            return byDistrictAndTheme.map(CafeInfoDto::fromEntity);
        } catch (IllegalArgumentException e) {
            throw new WrongDistrictAndTheme();
        }
    }

}
