package CafeFinder.cafe.service.cafe;

import CafeFinder.cafe.domain.GuReviewStats;
import CafeFinder.cafe.dto.GuReviewStatsDto;
import CafeFinder.cafe.repository.GuReviewStatsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuReviewStatsService {

    private final GuReviewStatsRepository guReviewStatsRepository;

    @Transactional
    public void saveGuReviewStats(List<GuReviewStats> stats) {
        guReviewStatsRepository.saveAll(stats);
    }

    @Transactional(readOnly = true)
    public List<GuReviewStatsDto> getAllStats() {
        return guReviewStatsRepository.findAllByOrderByAverageRatingDesc()
                .stream()
                .map(GuReviewStatsDto::fromEntity)  // DTO 변환
                .toList();
    }

    @Transactional(readOnly = true)
    public long countGuReviewStats() {
        return guReviewStatsRepository.count();
    }

}
