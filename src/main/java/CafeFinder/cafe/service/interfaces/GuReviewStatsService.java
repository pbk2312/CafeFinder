package CafeFinder.cafe.service.interfaces;

import CafeFinder.cafe.domain.GuReviewStats;
import CafeFinder.cafe.dto.GuReviewStatsDto;
import java.util.List;

public interface GuReviewStatsService {

    void saveGuReviewStats(List<GuReviewStats> stats);

    List<GuReviewStatsDto> getAllStats();

    long countGuReviewStats();

}
