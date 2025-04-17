package CafeFinder.cafe.cafe.service;


import CafeFinder.cafe.cafe.domain.SeoulDistrictStatus;
import CafeFinder.cafe.cafe.dto.SeoulDistrictStatusDto;
import CafeFinder.cafe.cafe.repository.SeoulDistrictRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeoulDistrictStatusSerciceImpl implements SeoulDistrictStatusService {

    private final SeoulDistrictRepository seoulDistrictRepository;

    @Override
    @Transactional
    public void saveGuReviewStats(List<SeoulDistrictStatus> stats) {
        seoulDistrictRepository.saveAll(stats);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeoulDistrictStatusDto> getAllStats() {
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
