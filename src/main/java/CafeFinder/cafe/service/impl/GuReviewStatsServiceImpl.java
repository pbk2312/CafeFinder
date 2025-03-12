package CafeFinder.cafe.service.impl;


import CafeFinder.cafe.domain.GuReviewStats;
import CafeFinder.cafe.dto.GuReviewStatsDto;
import CafeFinder.cafe.repository.GuReviewStatsRepository;
import CafeFinder.cafe.service.interfaces.GuReviewStatsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class GuReviewStatsServiceImpl implements GuReviewStatsService {

    private final GuReviewStatsRepository guReviewStatsRepository;

    @Override
    @Transactional
    public void saveGuReviewStats(List<GuReviewStats> stats) {
        guReviewStatsRepository.saveAll(stats);
    }

    @Override
    @Cacheable(value = "guReviewStatsCache")
    @Transactional(readOnly = true)
    public List<GuReviewStatsDto> getAllStats() {
        log.info("구별 통계 조회");
        return guReviewStatsRepository.findSortedByRatingAndReviewCount()
                .stream()
                .map(GuReviewStatsDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countGuReviewStats() {
        return guReviewStatsRepository.count();
    }

}
