package CafeFinder.cafe.service.interfaces;

import CafeFinder.cafe.domain.CafeInfo;
import CafeFinder.cafe.dto.CafeInfoDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CafeInfoService {

    void saveCafes(List<CafeInfo> cafes);

    long countCafes();

    Page<CafeInfoDto> getCafesByDistrictAndTheme(String district, String theme, Pageable pageable);

    Page<CafeInfoDto> searchCafesByNameOrAddress(String keyword, Pageable pageable);

    CafeInfoDto getCafeInfo(String cafeCode);
    
}
