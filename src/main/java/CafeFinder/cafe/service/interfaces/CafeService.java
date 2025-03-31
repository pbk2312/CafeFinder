package CafeFinder.cafe.service.interfaces;

import CafeFinder.cafe.domain.Cafe;
import CafeFinder.cafe.dto.CafeDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CafeService {

    void saveCafes(List<Cafe> cafes);

    long countCafes();

    Page<CafeDto> getCafesByDistrictAndTheme(String district, String theme, Pageable pageable);

    Page<CafeDto> searchCafesByNameOrAddress(String keyword, Pageable pageable);

    CafeDto getCafe(String cafeCode);

    List<CafeDto> getTopCafesByDistrictAndTheme(String district, String theme);

    List<CafeDto> getMostClickedCafes(String district, String theme);

    List<CafeDto> findCafesByDistance(double latitude, double longitude);

}
