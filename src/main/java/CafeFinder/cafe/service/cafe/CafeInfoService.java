package CafeFinder.cafe.service.cafe;

import CafeFinder.cafe.domain.CafeInfo;
import CafeFinder.cafe.domain.CafeInfoDocument;
import CafeFinder.cafe.dto.CafeInfoDto;
import CafeFinder.cafe.dto.CafeReviewDto;
import CafeFinder.cafe.elasticSearch.CafeInfoSearchRepository;
import CafeFinder.cafe.exception.CafeInfoNotFoundException;
import CafeFinder.cafe.exception.WrongCafeNameException;
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
    private final CafeInfoSearchRepository cafeInfoSearchRepository;

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
            log.info("district : {} , theme : {}", district, theme);

            // Elasticsearch에서 검색 수행
            Page<CafeInfoDocument> searchResults = cafeInfoSearchRepository.findByDistrictAndThemesContaining(district,
                    theme,
                    pageable);

            // 검색 결과를 DTO로 변환하여 반환
            return searchResults.map(CafeInfoDto::fromDocumentForList);
        } catch (IllegalArgumentException e) {
            throw new WrongDistrictAndTheme();
        }
    }

    @Transactional(readOnly = true)
    public Page<CafeInfoDto> getCafesByName(String name, Pageable pageable) {
        try {
            log.info("카페명 : {} ", name);
            Page<CafeInfoDocument> searchResults = cafeInfoSearchRepository.findByNameContaining(name, pageable);

            // 검색 결과를 DTO로 변환하여 반환
            return searchResults.map(CafeInfoDto::fromDocumentForList);
        } catch (IllegalArgumentException e) {
            throw new WrongCafeNameException();
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
