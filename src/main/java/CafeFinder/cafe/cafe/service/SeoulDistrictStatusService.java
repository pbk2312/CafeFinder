package CafeFinder.cafe.cafe.service;

import CafeFinder.cafe.cafe.domain.SeoulDistrictStatus;
import CafeFinder.cafe.cafe.dto.SeoulDistrictStatusDto;
import java.util.List;

public interface SeoulDistrictStatusService {

    void saveGuReviewStats(List<SeoulDistrictStatus> stats);

    List<SeoulDistrictStatusDto> getAllStats();

    long countSeoulDistrict();

}
