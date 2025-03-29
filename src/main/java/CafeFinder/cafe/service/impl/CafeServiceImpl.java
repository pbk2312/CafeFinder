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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CafeServiceImpl implements CafeService {

    private final CafeRepository cafeRepository;
    private final CafeSearchRepository cafeSearchRepository;

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
    @Cacheable("cafesByDistrictAndTheme")
    @Override
    public Page<CafeDto> getCafesByDistrictAndTheme(String district, String theme, Pageable pageable) {
        try {
            Page<IndexedCafe> searchResults = findCafesByDistrictAndTheme(district, theme, pageable);
            return searchResults.map(CafeDto::fromDocumentForList);
        } catch (IllegalArgumentException e) {
            throw new WrongDistrictAndTheme();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CafeDto> searchCafesByNameOrAddress(String keyword, Pageable pageable) {
        try {
            Page<IndexedCafe> searchResults = findCafesByNameOrAddress(keyword, pageable);
            return searchResults.map(CafeDto::fromDocumentForList);
        } catch (IllegalArgumentException e) {
            throw new WrongSearchException();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public CafeDto getCafe(String cafeCode) {
        Cafe cafe = findCafeByCafeCode(cafeCode);
        List<CafeReviewDto> reviewDtos = convertReviewsToDto(cafe);
        return CafeDto.fromEntityWithReviews(cafe, reviewDtos);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CafeDto> getTopCafesByDistrictAndTheme(String district, String theme) {
        return getRecommandationCafes(district, theme);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CafeDto> getMostClickedCafes(String district, String theme) {
        return getRecommandationCafes(district, theme);
    }

    private List<CafeDto> getRecommandationCafes(String district, String theme) {
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
