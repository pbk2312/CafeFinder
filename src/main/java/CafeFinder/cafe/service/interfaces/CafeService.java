package CafeFinder.cafe.service.interfaces;

import CafeFinder.cafe.domain.Cafe;
import CafeFinder.cafe.dto.CafeDto;
import CafeFinder.cafe.dto.CafeReviewsResponseDto;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CafeService {

    CompletableFuture<CafeReviewsResponseDto> getCafeReviewsAsync(String cafeCode, int page);

    void saveCafes(List<Cafe> cafes);

    long countCafes();

    Page<CafeDto> getCafesByDistrictAndTheme(String district, String theme, Pageable pageable);

    Page<CafeDto> searchCafesByNameOrAddress(String keyword, Pageable pageable);

    CafeDto getCafe(String cafeCode);

    List<CafeDto> getTopCafesByDistrictAndTheme(String district, String theme);

    List<CafeDto> getMostClickedCafes(String district, String theme);

    List<CafeDto> findCafesByDistance(double latitude, double longitude);

}
