package CafeFinder.cafe.repository;

import CafeFinder.cafe.domain.SeoulDistrictStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SeoulDistrictRepository extends JpaRepository<SeoulDistrictStatus, String> {

    @Query("SELECT A FROM SeoulDistrictStatus A ORDER BY A.averageRating DESC ,A.totalReviews DESC ")
    List<SeoulDistrictStatus> findSortedByRatingAndReviewCount();

}
