package CafeFinder.cafe.repository;

import CafeFinder.cafe.domain.SeoulDistrictStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SeoulDistrictRepository extends JpaRepository<SeoulDistrictStatus, String> {

    // 평점이 높은 순서대로, 평점이 같으면 리뷰 갯수대로
    @Query("SELECT A FROM SeoulDistrictStatus A ORDER BY A.averageRating DESC ,A.totalReviews DESC ")
    List<SeoulDistrictStatus> findSortedByRatingAndReviewCount();

}
