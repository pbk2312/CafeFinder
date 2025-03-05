package CafeFinder.cafe.repository;

import CafeFinder.cafe.domain.CafeDistrict;
import CafeFinder.cafe.domain.CafeInfo;
import CafeFinder.cafe.domain.CafeTheme;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CafeInfoRepository extends JpaRepository<CafeInfo, String> {

    // 구,테마 별로 검색하기
    Page<CafeInfo> findByDistrictAndThemesContaining(CafeDistrict district, CafeTheme theme, Pageable pageable);

    // N+1 문제 방지
    @Query("select distinct c from CafeInfo c left join fetch c.reviews where c.cafeCode = :cafeCode")
    Optional<CafeInfo> findByCafeCodeWithReviews(@Param("cafeCode") String cafeCode);

}
