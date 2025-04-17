package CafeFinder.cafe.cafe.repository;

import CafeFinder.cafe.cafe.domain.CafeReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeReviewRepository extends JpaRepository<CafeReview, Long> {

    Page<CafeReview> findByCafe_Code(String cafeCode, Pageable pageable);

}
