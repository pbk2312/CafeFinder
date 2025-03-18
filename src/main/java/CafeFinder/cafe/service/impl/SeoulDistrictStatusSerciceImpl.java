package CafeFinder.cafe.service.impl;


import CafeFinder.cafe.domain.SeoulDistrictStatus;
import CafeFinder.cafe.dto.SeoulDistrictStatusDto;
import CafeFinder.cafe.repository.SeoulDistrictRepository;
import CafeFinder.cafe.service.interfaces.SeoulDistrictStatusService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class SeoulDistrictStatusSerciceImpl implements SeoulDistrictStatusService {

    private final SeoulDistrictRepository seoulDistrictRepository;

    @Override
    @Transactional
    public void saveGuReviewStats(List<SeoulDistrictStatus> stats) {
        seoulDistrictRepository.saveAll(stats);
    }

    @Override
    @Cacheable(value = "seoulDistrictStatus")
    @Transactional(readOnly = true)
    public List<SeoulDistrictStatusDto> getAllStats() {
        log.info("구별 통계 조회");
        return seoulDistrictRepository.findSortedByRatingAndReviewCount()
                .stream()
                .map(SeoulDistrictStatusDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countSeoulDistrict() {
        return seoulDistrictRepository.count();
    }

}
