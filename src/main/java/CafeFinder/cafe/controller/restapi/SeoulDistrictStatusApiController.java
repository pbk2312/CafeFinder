package CafeFinder.cafe.controller.restapi;

import static CafeFinder.cafe.util.ResponseMessage.GUREVIEW_STATS_SUCCESS;

import CafeFinder.cafe.dto.ResponseDto;
import CafeFinder.cafe.dto.SeoulDistrictStatusDto;
import CafeFinder.cafe.service.interfaces.SeoulDistrictStatusService;
import CafeFinder.cafe.util.ResponseUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SeoulDistrictStatusApiController {

    private final SeoulDistrictStatusService seoulDistrictStatusService;

    @GetMapping("/seoulDistrictStatus")
    public ResponseEntity<ResponseDto<List<SeoulDistrictStatusDto>>> getSeoulDistrictStatus() {
        List<SeoulDistrictStatusDto> guReviews = seoulDistrictStatusService.getAllStats();
        return ResponseUtil.buildResponse(HttpStatus.OK, GUREVIEW_STATS_SUCCESS.getMessage(), guReviews, true);
    }

}
