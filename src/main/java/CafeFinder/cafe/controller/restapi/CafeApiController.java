package CafeFinder.cafe.controller.restapi;

import CafeFinder.cafe.dto.CafeInfoDto;
import CafeFinder.cafe.dto.GuReviewStatsDto;
import CafeFinder.cafe.dto.ResponseDto;
import CafeFinder.cafe.dto.ThemeDto;
import CafeFinder.cafe.service.interfaces.CafeInfoService;
import CafeFinder.cafe.service.interfaces.CafeThemeService;
import CafeFinder.cafe.service.interfaces.GuReviewStatsService;
import CafeFinder.cafe.util.ResponseUtil;
import CafeFinder.cafe.util.ViewMessage;
import static CafeFinder.cafe.util.ViewMessage.DISTRCT_THEME_GET_SUCCESS;
import static CafeFinder.cafe.util.ViewMessage.GET_CAFE_THEME;
import static CafeFinder.cafe.util.ViewMessage.GUREVIEW_STATS_SUCCESS;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cafes")
@Log4j2
public class CafeApiController {

    private final GuReviewStatsService guReviewStatsService;
    private final CafeInfoService cafeInfoService;
    private final CafeThemeService cafeThemeService;

    @GetMapping("/guReviewStats")
    public ResponseEntity<ResponseDto<List<GuReviewStatsDto>>> getGuReviewStats() {
        List<GuReviewStatsDto> guReviews = guReviewStatsService.getAllStats();
        return ResponseUtil.buildResponse(GUREVIEW_STATS_SUCCESS.getMessage(), guReviews, true);
    }

    @GetMapping("/theme")
    public ResponseEntity<ResponseDto<List<ThemeDto>>> getCafeThemes() {
        List<ThemeDto> themeList = cafeThemeService.getCafeThemes();
        return ResponseUtil.buildResponse(GET_CAFE_THEME.getMessage(), themeList, true);
    }

    @GetMapping("/district/{district}/{theme}")
    public ResponseEntity<ResponseDto<Page<CafeInfoDto>>> getCafesByDistrictAndTheme(
            @PathVariable String district,
            @PathVariable String theme,
            @PageableDefault(size = 9) Pageable pageable) {

        return ResponseUtil.buildResponse(
                DISTRCT_THEME_GET_SUCCESS.getMessage(),
                cafeInfoService.getCafesByDistrictAndTheme(district, theme, pageable),
                true);
    }

    @GetMapping("/{cafeCode}")
    public ResponseEntity<ResponseDto<CafeInfoDto>> getCafeInfo(@PathVariable String cafeCode) {
        CafeInfoDto cafeInfoDto = cafeInfoService.getCafeInfo(cafeCode);
        return ResponseUtil.buildResponse(GET_CAFE_THEME.getMessage(), cafeInfoDto, true);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseDto<Page<CafeInfoDto>>> searchCafes(
            @RequestParam("keyword") String keyword,
            @PageableDefault(size = 9) Pageable pageable) {

        Page<CafeInfoDto> result = cafeInfoService.searchCafesByNameOrAddress(keyword, pageable);
        return ResponseUtil.buildResponse(ViewMessage.GET_CAFE_INFO_LIST_BY_NAME.getMessage(), result, true);
    }

}
