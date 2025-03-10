package CafeFinder.cafe.repository;

import CafeFinder.cafe.domain.CafeInfo;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CafeInfoRepository extends JpaRepository<CafeInfo, String> {
    
    // N+1 문제 방지
    @Query("select distinct c from CafeInfo c left join fetch c.reviews where c.cafeCode = :cafeCode")
    Optional<CafeInfo> findByCafeCodeWithReviews(@Param("cafeCode") String cafeCode);

}
