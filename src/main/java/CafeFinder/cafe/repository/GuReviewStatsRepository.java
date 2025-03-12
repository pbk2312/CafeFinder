package CafeFinder.cafe.repository;

import CafeFinder.cafe.domain.GuReviewStats;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GuReviewStatsRepository extends JpaRepository<GuReviewStats, String> {

    // 평점이 높은 순서대로, 평점이 같으면 리뷰 갯수대로
    @Query("SELECT A FROM GuReviewStats A ORDER BY A.averageRating DESC ,A.totalReviews DESC ")
    List<GuReviewStats> findSortedByRatingAndReviewCount();

}
