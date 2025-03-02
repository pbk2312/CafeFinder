package CafeFinder.cafe.controller.restapi;

import static CafeFinder.cafe.util.ViewMessage.DISTRCT_THEME_GET_SUCCESS;
import static CafeFinder.cafe.util.ViewMessage.GUREVIEW_STATS_SUCCESS;

import CafeFinder.cafe.dto.CafeInfoDto;
import CafeFinder.cafe.dto.GuReviewStatsDto;
import CafeFinder.cafe.dto.ResponseDto;
import CafeFinder.cafe.service.cafe.CafeInfoService;
import CafeFinder.cafe.service.cafe.GuReviewStatsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cafes")
public class CafeApiController {

    private final GuReviewStatsService guReviewStatsService;
    private final CafeInfoService cafeInfoService;

    @GetMapping("/guReviewStats")
    public ResponseEntity<ResponseDto<List<GuReviewStatsDto>>> getGuReviewStats() {
        List<GuReviewStatsDto> guReviews = guReviewStatsService.getAllStats();

        return ResponseEntity.ok(new ResponseDto<>(GUREVIEW_STATS_SUCCESS.getMessage(), guReviews, true));
    }

    @GetMapping("/district/{distric}/{theme}")
    public ResponseEntity<ResponseDto<Page<CafeInfoDto>>> getCafeInfoByDistrictAndTheme(
            @PathVariable String distric,
            @PathVariable String theme,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<CafeInfoDto> cafeInfoDtos = cafeInfoService.getCafesByDistrictAndTheme(distric, theme,
                pageable);

        return ResponseEntity.ok(
                new ResponseDto<>(DISTRCT_THEME_GET_SUCCESS.getMessage(), cafeInfoDtos, true));
    }

}
