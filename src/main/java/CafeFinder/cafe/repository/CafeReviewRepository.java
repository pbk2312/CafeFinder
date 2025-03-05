package CafeFinder.cafe.repository;

import CafeFinder.cafe.domain.CafeReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeReviewRepository extends JpaRepository<CafeReview, Long> {
}
