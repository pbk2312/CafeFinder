package CafeFinder.cafe.service.impl;

import CafeFinder.cafe.domain.Cafe;
import CafeFinder.cafe.dto.CafeDto;
import CafeFinder.cafe.dto.CafeReviewDto;
import CafeFinder.cafe.exception.CafeNotFoundException;
import CafeFinder.cafe.exception.WrongDistrictAndTheme;
import CafeFinder.cafe.exception.WrongSearchException;
import CafeFinder.cafe.infrastructure.elasticSearch.CafeSearchRepository;
import CafeFinder.cafe.infrastructure.elasticSearch.IndexedCafe;
import CafeFinder.cafe.repository.CafeRepository;
import CafeFinder.cafe.service.interfaces.CafeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class CafeServiceImpl implements CafeService {

    private final CafeRepository cafeRepository;
    private final CafeSearchRepository cafeSearchRepository;

    @Transactional
    @Override
    public void saveCafes(List<Cafe> cafes) {
        cafeRepository.saveAll(cafes);
        log.info("카페 {}개 저장 완료", cafes.size());
    }

    @Transactional(readOnly = true)
    @Override
    public long countCafes() {
        long count = cafeRepository.count();
        log.info("총 카페 수: {}", count);
        return count;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CafeDto> getCafesByDistrictAndTheme(String district, String theme, Pageable pageable) {
        log.info("행정구: {} 및 테마: {}에 해당하는 카페 조회 시작", district, theme);
        try {
            Page<IndexedCafe> searchResults = findCafesByDistrictAndTheme(district, theme, pageable);
            return searchResults.map(CafeDto::fromDocumentForList);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 행정구나 테마 입력: {} / {}", district, theme);
            throw new WrongDistrictAndTheme();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CafeDto> searchCafesByNameOrAddress(String keyword, Pageable pageable) {
        log.info("검색어(카페명/주소): {}를 이용하여 카페 검색 시작", keyword);
        try {
            Page<IndexedCafe> searchResults = findCafesByNameOrAddress(keyword, pageable);
            return searchResults.map(CafeDto::fromDocumentForList);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 검색어 입력: {}", keyword);
            throw new WrongSearchException();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public CafeDto getCafe(String cafeCode) {
        log.info("카페 상세 정보 조회 시작: {}", cafeCode);
        Cafe cafe = findCafeByCafeCode(cafeCode);
        log.info("카페 정보 조회 성공: {}", cafeCode);
        List<CafeReviewDto> reviewDtos = convertReviewsToDto(cafe);
        log.info("리뷰 수: {}", reviewDtos.size());
        return CafeDto.fromEntityWithReviews(cafe, reviewDtos);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CafeDto> getTopCafesByDistrictAndTheme(String district, String theme) {
        log.info("상위 카페 조회 시작: 행정구={}, 테마={}", district, theme);

        Page<IndexedCafe> page = findCafesByDistrictAndTheme(district, theme, PageRequest.of(0, 6));

        return page.getContent().stream()
                .map(CafeDto::fromDocumentForList)
                .toList();
    }


    private Page<IndexedCafe> findCafesByNameOrAddress(String keyword, Pageable pageable) {
        return cafeSearchRepository.findByNameContainingOrAddressContaining(keyword, keyword, pageable);
    }

    private Page<IndexedCafe> findCafesByDistrictAndTheme(String district, String theme, Pageable pageable) {

        Sort sort = Sort.by(
                Sort.Order.desc("averageRating"),
                Sort.Order.desc("reviewCount")
        );

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return cafeSearchRepository.findByDistrictAndThemesContaining(district, theme, sortedPageable);
    }

    private static List<CafeReviewDto> convertReviewsToDto(Cafe cafe) {
        return cafe.getReviews().stream()
                .map(CafeReviewDto::fromEntity)
                .toList();
    }

    private Cafe findCafeByCafeCode(String cafeCode) {
        return cafeRepository.findByCafeCodeWithReviews(cafeCode)
                .orElseThrow(() -> new CafeNotFoundException(cafeCode));
    }

}
