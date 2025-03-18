package CafeFinder.cafe.service.interfaces;

import CafeFinder.cafe.domain.SeoulDistrictStatus;
import CafeFinder.cafe.dto.SeoulDistrictStatusDto;
import java.util.List;

public interface SeoulDistrictStatusService {

    void saveGuReviewStats(List<SeoulDistrictStatus> stats);

    List<SeoulDistrictStatusDto> getAllStats();

    long countSeoulDistrict();

}
