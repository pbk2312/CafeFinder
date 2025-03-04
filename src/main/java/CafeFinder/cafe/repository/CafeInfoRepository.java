package CafeFinder.cafe.repository;

import CafeFinder.cafe.domain.CafeDistrict;
import CafeFinder.cafe.domain.CafeInfo;
import CafeFinder.cafe.domain.CafeTheme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeInfoRepository extends JpaRepository<CafeInfo, String> {

    // 구,테마 별로 검색하기
    Page<CafeInfo> findByDistrictAndThemesContaining(CafeDistrict district, CafeTheme theme, Pageable pageable);


}
