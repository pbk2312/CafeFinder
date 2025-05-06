package CafeFinder.cafe.cafe.service;

import static CafeFinder.cafe.global.exception.ErrorCode.INVALID_CAFE_SEARCH;
import static CafeFinder.cafe.global.exception.ErrorCode.INVALID_DISTRICT_THEME;
import static CafeFinder.cafe.global.exception.ErrorCode.NOT_FOUND_CAFE;

import CafeFinder.cafe.cafe.domain.Cafe;
import CafeFinder.cafe.cafe.domain.CafeReview;
import CafeFinder.cafe.cafe.dto.CafeDto;
import CafeFinder.cafe.cafe.dto.CafeMapDto;
import CafeFinder.cafe.cafe.dto.CafeReviewDto;
import CafeFinder.cafe.cafe.dto.CafeReviewsResponseDto;
import CafeFinder.cafe.cafe.repository.CafeRepository;
import CafeFinder.cafe.cafe.repository.CafeReviewRepository;
import CafeFinder.cafe.global.exception.ErrorException;
import CafeFinder.cafe.global.infrastructure.elasticSearch.CafeSearchRepository;
import CafeFinder.cafe.global.infrastructure.elasticSearch.IndexedCafe;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CafeServiceImpl implements CafeService {

    private static final Sort DEFAULT_SORT = Sort.by(
        Sort.Order.desc("averageRating"),
        Sort.Order.desc("reviewCount")
    );

    private static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 6, DEFAULT_SORT);
    private static final String DISTANCE_RADIUS = "1km";

    private final CafeRepository cafeRepository;

    private final CafeSearchRepository cafeSearchRepository;
    private final CafeReviewRepository cafeReviewRepository;


    @Transactional(readOnly = true)
    @Override
    public CafeDto getCafe(String cafeCode) {
        Cafe cafe = findCafeByCafeCode(cafeCode);
        return CafeDto.fromEntity(cafe);
    }

    private Cafe findCafeByCafeCode(String cafeCode) {
        return cafeRepository.findCafeByCode(cafeCode)
            .orElseThrow(() -> new ErrorException(NOT_FOUND_CAFE));
    }


    @Async
    @Transactional(readOnly = true)
    @Override
    public CompletableFuture<CafeReviewsResponseDto> getCafeReviewsAsync(String cafeCode,
        int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id")); // 최신순
        Page<CafeReview> pageResult = cafeReviewRepository.findByCafe_Code(cafeCode, pageable);

        List<CafeReviewDto> dtos = pageResult.stream()
            .map(CafeReviewDto::fromEntity)
            .toList();

        int totalReviewCount = (int) pageResult.getTotalElements();

        CafeReviewsResponseDto responseDto = new CafeReviewsResponseDto(dtos, totalReviewCount);
        return CompletableFuture.completedFuture(responseDto);
    }


    @Transactional
    @Override
    public void saveCafes(List<Cafe> cafes) {
        cafeRepository.saveAll(cafes);
    }

    @Transactional(readOnly = true)
    @Override
    public long countCafes() {
        return cafeRepository.count();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CafeDto> getCafesByDistrictAndTheme(String district, String theme,
        Pageable pageable) {
        try {
            Page<IndexedCafe> searchResults = findCafesByDistrictAndTheme(district, theme,
                pageable);
            return searchResults.map(CafeDto::fromDocumentForList);
        } catch (IllegalArgumentException e) {
            throw new ErrorException(INVALID_DISTRICT_THEME);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CafeDto> searchCafesByNameOrAddress(String keyword, Pageable pageable) {
        try {
            Page<IndexedCafe> searchResults = findCafesByNameOrAddress(keyword, pageable);
            return searchResults.map(CafeDto::fromDocumentForList);
        } catch (IllegalArgumentException e) {
            throw new ErrorException(INVALID_CAFE_SEARCH);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<CafeDto> getTopCafesByDistrictAndTheme(String district, String theme) {
        return getRecommendationCafes(district, theme);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CafeDto> getMostClickedCafes(String district, String theme) {
        return getRecommendationCafes(district, theme);
    }

    @Override
    public List<CafeMapDto> findCafesByDistance(double latitude, double longitude) {
        GeoPoint userLocation = new GeoPoint(latitude, longitude);
        log.info("사용자 위치: {}", userLocation);

        List<IndexedCafe> distanceCafes = cafeSearchRepository.findCafesNearLocation(latitude,
            DISTANCE_RADIUS,
            longitude);
        log.info("사용자 위치 근처 카페 수: {}", distanceCafes.size());

        return distanceCafes.stream()
            .map(CafeMapDto::fromIndexDocument)
            .toList();
    }

    private List<CafeDto> getRecommendationCafes(String district, String theme) {
        Page<IndexedCafe> page = findCafesByDistrictAndTheme(district, theme, DEFAULT_PAGEABLE);
        return page.getContent().stream()
            .map(CafeDto::fromDocumentForList)
            .toList();
    }

    private Page<IndexedCafe> findCafesByNameOrAddress(String keyword, Pageable pageable) {
        return cafeSearchRepository.findByNameContainingOrAddressContaining(keyword, keyword,
            pageable);
    }

    private Page<IndexedCafe> findCafesByDistrictAndTheme(String district, String theme,
        Pageable pageable) {
        return cafeSearchRepository.findByDistrictAndThemesContaining(district, theme, pageable);
    }

}
