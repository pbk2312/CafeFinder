package CafeFinder.cafe.repository;

import CafeFinder.cafe.domain.Cafe;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CafeRepository extends JpaRepository<Cafe, String> {

    // N+1 문제 방지
    @Query("select distinct c from Cafe c left join fetch c.reviews where c.code = :cafeCode")
    Optional<Cafe> findByCafeCodeWithReviews(@Param("cafeCode") String cafeCode);

}
