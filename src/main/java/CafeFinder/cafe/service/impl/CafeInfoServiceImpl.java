package CafeFinder.cafe.service.impl;


import CafeFinder.cafe.domain.CafeInfo;
import CafeFinder.cafe.domain.CafeInfoDocument;
import CafeFinder.cafe.dto.CafeInfoDto;
import CafeFinder.cafe.dto.CafeReviewDto;
import CafeFinder.cafe.elasticSearch.CafeInfoSearchRepository;
import CafeFinder.cafe.exception.CafeInfoNotFoundException;
import CafeFinder.cafe.exception.WrongDistrictAndTheme;
import CafeFinder.cafe.exception.WrongSearchException;
import CafeFinder.cafe.repository.CafeInfoRepository;
import CafeFinder.cafe.service.interfaces.CafeInfoService;
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
public class CafeInfoServiceImpl implements CafeInfoService {

    private final CafeInfoRepository cafeInfoRepository;
    private final CafeInfoSearchRepository cafeInfoSearchRepository;

    @Transactional
    @Override
    public void saveCafes(List<CafeInfo> cafes) {
        cafeInfoRepository.saveAll(cafes);
    }

    @Transactional(readOnly = true)
    @Override
    public long countCafes() {
        return cafeInfoRepository.count();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CafeInfoDto> getCafesByDistrictAndTheme(String district, String theme, Pageable pageable) {
        try {
            log.info("district : {} , theme : {}", district, theme);
            Page<CafeInfoDocument> searchResults = cafeInfoSearchRepository.findByDistrictAndThemesContainingOrderByReviewDesc(
                    district,
                    theme, pageable);
            return searchResults.map(CafeInfoDto::fromDocumentForList);
        } catch (IllegalArgumentException e) {
            throw new WrongDistrictAndTheme();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CafeInfoDto> searchCafesByNameOrAddress(String keyword, Pageable pageable) {
        try {
            log.info("검색어(카페명/주소) : {}", keyword);
            Page<CafeInfoDocument> searchResults = cafeInfoSearchRepository.findByNameContainingOrAddressContaining(
                    keyword, keyword, pageable);
            return searchResults.map(CafeInfoDto::fromDocumentForList);
        } catch (IllegalArgumentException e) {
            throw new WrongSearchException();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public CafeInfoDto getCafeInfo(String cafeCode) {
        log.info("카페 상세정보 조회 : {}", cafeCode);
        CafeInfo cafeInfo = cafeInfoRepository.findByCafeCodeWithReviews(cafeCode)
                .orElseThrow(() -> new CafeInfoNotFoundException(cafeCode));

        List<CafeReviewDto> reviewDtos = cafeInfo.getReviews().stream()
                .map(CafeReviewDto::fromEntity)
                .toList();

        return CafeInfoDto.fromEntityWithReviews(cafeInfo, reviewDtos);
    }

}
