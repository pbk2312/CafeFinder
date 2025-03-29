package CafeFinder.cafe.controller.restapi;

import static CafeFinder.cafe.util.ResponseMessage.CLICK_EVENT_SUCCESS;
import static CafeFinder.cafe.util.ResponseMessage.DISTRCT_THEME_GET_SUCCESS;
import static CafeFinder.cafe.util.ResponseMessage.GET_CAFE_INFO;
import static CafeFinder.cafe.util.ResponseMessage.GET_CAFE_INFO_LIST_BY_NAME;
import static CafeFinder.cafe.util.ResponseMessage.GET_CAFE_THEME;
import static CafeFinder.cafe.util.ResponseMessage.GUREVIEW_STATS_SUCCESS;

import CafeFinder.cafe.dto.CafeDto;
import CafeFinder.cafe.dto.CafeThemeDto;
import CafeFinder.cafe.dto.ResponseDto;
import CafeFinder.cafe.dto.SeoulDistrictStatusDto;
import CafeFinder.cafe.infrastructure.kafka.CafeClickProducer;
import CafeFinder.cafe.service.interfaces.CafeService;
import CafeFinder.cafe.service.interfaces.CafeThemeService;
import CafeFinder.cafe.service.interfaces.RecommendationService;
import CafeFinder.cafe.service.interfaces.SeoulDistrictStatusService;
import CafeFinder.cafe.util.ResponseMessage;
import CafeFinder.cafe.util.ResponseUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cafes")
@Slf4j
public class CafeApiController {

    private final SeoulDistrictStatusService seoulDistrictStatusService;
    private final CafeService cafeService;
    private final CafeThemeService cafeThemeService;
    private final CafeClickProducer cafeClickProducer;
    private final RecommendationService recommendationService;

    @GetMapping("/guReviewStats")
    public ResponseEntity<ResponseDto<List<SeoulDistrictStatusDto>>> getSeoulDistrictStatus() {
        List<SeoulDistrictStatusDto> guReviews = seoulDistrictStatusService.getAllStats();
        return ResponseUtil.buildResponse(HttpStatus.OK, GUREVIEW_STATS_SUCCESS.getMessage(), guReviews, true);
    }

    @GetMapping("/theme")
    public ResponseEntity<ResponseDto<List<CafeThemeDto>>> getCafeThemes() {
        List<CafeThemeDto> themeList = cafeThemeService.getCafeThemes();
        return ResponseUtil.buildResponse(HttpStatus.OK, GET_CAFE_THEME.getMessage(), themeList, true);
    }

    @GetMapping("/district/{district}/{theme}")
    public ResponseEntity<ResponseDto<Page<CafeDto>>> getCafesByDistrictAndTheme(
            @PathVariable String district,
            @PathVariable String theme,
            @PageableDefault(size = 9) Pageable pageable) {

        Page<CafeDto> cafes = cafeService.getCafesByDistrictAndTheme(district, theme, pageable);
        return ResponseUtil.buildResponse(HttpStatus.OK, DISTRCT_THEME_GET_SUCCESS.getMessage(), cafes, true);
    }

    @GetMapping("/{cafeCode}")
    public ResponseEntity<ResponseDto<CafeDto>> getCafeInfo(@PathVariable String cafeCode) {
        CafeDto cafeInfoDto = cafeService.getCafe(cafeCode);
        return ResponseUtil.buildResponse(HttpStatus.OK, GET_CAFE_INFO.getMessage(), cafeInfoDto, true);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseDto<Page<CafeDto>>> searchCafes(
            @RequestParam("keyword") String keyword,
            @PageableDefault(size = 9) Pageable pageable) {

        Page<CafeDto> result = cafeService.searchCafesByNameOrAddress(keyword, pageable);
        return ResponseUtil.buildResponse(HttpStatus.OK, GET_CAFE_INFO_LIST_BY_NAME.getMessage(), result,
                true);
    }

    @PostMapping("/click/{cafeCode}")
    public ResponseEntity<ResponseDto<String>> clickCafe(
            @CookieValue(value = "accessToken", required = true) String accessToken,
            @PathVariable String cafeCode) {

        cafeClickProducer.sendCafeClickEvent(accessToken, cafeCode);
        return ResponseUtil.buildResponse(HttpStatus.ACCEPTED, CLICK_EVENT_SUCCESS.getMessage(), null, true);
    }

    @GetMapping("/mostClicked")
    public ResponseEntity<ResponseDto<List<CafeDto>>> getMostClicked() {
        List<CafeDto> cafeDtos = recommendationService.getGlobalRecommendationCafes();
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.MOST_CLICKED_CAFES.getMessage(), cafeDtos,
                true);
    }

}
