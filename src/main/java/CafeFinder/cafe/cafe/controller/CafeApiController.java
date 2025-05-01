package CafeFinder.cafe.cafe.controller;

import static CafeFinder.cafe.global.util.ResponseMessage.CAFE_REVIEWS_OK;
import static CafeFinder.cafe.global.util.ResponseMessage.CLICK_EVENT_SUCCESS;
import static CafeFinder.cafe.global.util.ResponseMessage.DISTRCT_THEME_GET_SUCCESS;
import static CafeFinder.cafe.global.util.ResponseMessage.GET_CAFE_INFO;
import static CafeFinder.cafe.global.util.ResponseMessage.GET_CAFE_INFO_LIST_BY_NAME;
import static CafeFinder.cafe.global.util.ResponseMessage.GET_CAFE_THEME;

import CafeFinder.cafe.cafe.dto.CafeDto;
import CafeFinder.cafe.cafe.dto.CafeMapDto;
import CafeFinder.cafe.cafe.dto.CafeReviewsResponseDto;
import CafeFinder.cafe.cafe.dto.CafeThemeDto;
import CafeFinder.cafe.cafe.service.CafeService;
import CafeFinder.cafe.cafe.service.CafeThemeService;
import CafeFinder.cafe.cafe.service.RecommendationService;
import CafeFinder.cafe.global.dto.ResponseDto;
import CafeFinder.cafe.global.infrastructure.kafka.CafeClickProducer;
import CafeFinder.cafe.global.util.ResponseMessage;
import CafeFinder.cafe.global.util.ResponseUtil;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    private final CafeService cafeService;
    private final CafeThemeService cafeThemeService;
    private final CafeClickProducer cafeClickProducer;
    private final RecommendationService recommendationService;

    @GetMapping("/{cafeCode}")
    public ResponseEntity<ResponseDto<CafeDto>> getCafe(@PathVariable String cafeCode) {
        CafeDto cafeInfoDto = cafeService.getCafe(cafeCode);
        return ResponseUtil.buildResponse(HttpStatus.OK, GET_CAFE_INFO.getMessage(), cafeInfoDto,
            true);
    }

    @GetMapping("/reviews/{cafeCode}")
    public CompletableFuture<ResponseEntity<ResponseDto<CafeReviewsResponseDto>>> getCafeReviews(
        @PathVariable String cafeCode,
        @RequestParam(defaultValue = "0") int page) {

        return cafeService.getCafeReviewsAsync(cafeCode, page)
            .thenApply(responseDto ->
                ResponseUtil.buildResponse(HttpStatus.OK, CAFE_REVIEWS_OK.getMessage(), responseDto,
                    true)
            );
    }

    @GetMapping("/themes")
    public ResponseEntity<ResponseDto<List<CafeThemeDto>>> getCafeThemes() {
        List<CafeThemeDto> themeList = cafeThemeService.getCafeThemes();
        return ResponseUtil.buildResponse(HttpStatus.OK, GET_CAFE_THEME.getMessage(), themeList,
            true);
    }

    @GetMapping("/{district}/{theme}")
    public ResponseEntity<ResponseDto<Page<CafeDto>>> getCafesByDistrictAndTheme(
        @PathVariable String district,
        @PathVariable String theme,
        @PageableDefault(size = 9) Pageable pageable) {

        Page<CafeDto> cafes = cafeService.getCafesByDistrictAndTheme(district, theme, pageable);
        return ResponseUtil.buildResponse(HttpStatus.OK, DISTRCT_THEME_GET_SUCCESS.getMessage(),
            cafes, true);
    }


    @GetMapping("/search")
    public ResponseEntity<ResponseDto<Page<CafeDto>>> searchCafes(
        @RequestParam("keyword") String keyword,
        @PageableDefault(size = 9) Pageable pageable) {

        Page<CafeDto> result = cafeService.searchCafesByNameOrAddress(keyword, pageable);
        return ResponseUtil.buildResponse(HttpStatus.OK, GET_CAFE_INFO_LIST_BY_NAME.getMessage(),
            result,
            true);
    }

    @PostMapping("/click/{cafeCode}")
    public ResponseEntity<ResponseDto<String>> clickCafe(
        @PathVariable String cafeCode) {

        cafeClickProducer.sendCafeClickEvent(cafeCode);
        return ResponseUtil.buildResponse(HttpStatus.ACCEPTED, CLICK_EVENT_SUCCESS.getMessage(),
            null, true);
    }

    @GetMapping("/mostClicked")
    public ResponseEntity<ResponseDto<List<CafeDto>>> getMostClicked() {
        List<CafeDto> cafeDtos = recommendationService.getGlobalRecommendationCafes();
        return ResponseUtil.buildResponse(HttpStatus.OK,
            ResponseMessage.MOST_CLICKED_CAFES.getMessage(), cafeDtos,
            true);
    }

    @PostMapping("/by-distance")
    public ResponseEntity<ResponseDto<List<CafeMapDto>>> getCafesSortedByDistance(
        @RequestParam double latitude,
        @RequestParam double longitude) {
        List<CafeMapDto> cafeDtos = cafeService.findCafesByDistance(latitude, longitude);
        return ResponseUtil.buildResponse(HttpStatus.OK,
            ResponseMessage.GET_CAFES_BY_DISTANCE.getMessage(), cafeDtos,
            true);
    }
}
