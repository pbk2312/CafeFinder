package CafeFinder.cafe.repository;

import CafeFinder.cafe.domain.GuReviewStats;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuReviewStatsRepository extends JpaRepository<GuReviewStats, String> {

    // 평점 높은 순서대로
    List<GuReviewStats> findAllByOrderByAverageRatingDesc();

}
