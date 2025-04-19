package CafeFinder.cafe.member.controller;

import static CafeFinder.cafe.global.util.ResponseMessage.GET_RECOMMAND_CAFES;
import static CafeFinder.cafe.global.util.ResponseMessage.LOGIN_SUCCESS;
import static CafeFinder.cafe.global.util.ResponseMessage.LOGOUT_SUCCESS;
import static CafeFinder.cafe.global.util.ResponseMessage.NOT_LOGIN;
import static CafeFinder.cafe.global.util.ResponseMessage.POST_SCRAP_OK;
import static CafeFinder.cafe.global.util.ResponseMessage.PROFILE_INFO;
import static CafeFinder.cafe.global.util.ResponseMessage.SIGN_UP_SUCCESS;
import static CafeFinder.cafe.global.util.ResponseMessage.UPDATE_PROFILE;

import CafeFinder.cafe.cafe.dto.CafeDto;
import CafeFinder.cafe.cafe.dto.CafeScrapDto;
import CafeFinder.cafe.cafe.dto.ScrapCafeCodeDto;
import CafeFinder.cafe.cafe.service.RecommendationService;
import CafeFinder.cafe.global.dto.ResponseDto;
import CafeFinder.cafe.global.util.ResponseMessage;
import CafeFinder.cafe.global.util.ResponseUtil;
import CafeFinder.cafe.member.dto.AccessTokenDto;
import CafeFinder.cafe.member.dto.MemberLoginDto;
import CafeFinder.cafe.member.dto.MemberProfileDto;
import CafeFinder.cafe.member.dto.MemberSignUpDto;
import CafeFinder.cafe.member.dto.MemberUpdateDto;
import CafeFinder.cafe.member.dto.ProfileDto;
import CafeFinder.cafe.member.dto.RefreshTokenDto;
import CafeFinder.cafe.member.dto.TokenDto;
import CafeFinder.cafe.member.dto.TokenRequestDto;
import CafeFinder.cafe.member.dto.TokenResultDto;
import CafeFinder.cafe.member.service.AuthenticationService;
import CafeFinder.cafe.member.service.CafeScrapService;
import CafeFinder.cafe.member.service.MemberService;
import CafeFinder.cafe.member.service.ProfileService;
import CafeFinder.cafe.member.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final AuthenticationService authenticationService;
    private final RecommendationService recommendationService;
    private final ProfileService profileService;
    private final CafeScrapService cafeScrapService;

    @PostMapping("/signUp")
    public ResponseEntity<ResponseDto<String>> signUp(
            @Valid @ModelAttribute MemberSignUpDto memberSignUpDto
    ) {
        memberService.save(memberSignUpDto);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, SIGN_UP_SUCCESS.getMessage(), null, true);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<String>> login(
            @Valid @RequestBody MemberLoginDto loginDto,
            HttpServletResponse response, HttpServletRequest request) {
        TokenDto tokenDto = authenticationService.login(loginDto);
        CookieUtils.addCookie(response, "accessToken", tokenDto.getAccessToken(),
                tokenDto.getAccessTokenExpiresIn());
        CookieUtils.addCookie(response, "refreshToken", tokenDto.getRefreshToken(),
                tokenDto.getRefreshTokenExpiresIn());
        String redirectUrl = getRedirectUrlFromSession(request);
        return ResponseUtil.buildResponse(HttpStatus.OK, LOGIN_SUCCESS.getMessage(), redirectUrl, true);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<String>> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        RefreshTokenDto refreshTokenDto = createRefreshTokenDto(refreshToken);

        CookieUtils.removeCookie(response, "accessToken");
        CookieUtils.removeCookie(response, "refreshToken");

        authenticationService.logout(refreshTokenDto);

        return ResponseUtil.buildResponse(HttpStatus.OK, LOGOUT_SUCCESS.getMessage(), null, true);
    }

    @PatchMapping("/update")
    public ResponseEntity<ResponseDto<String>> update(
            @Valid MemberUpdateDto userUpdateDto
    ) {
        profileService.update(userUpdateDto);
        return ResponseUtil.buildResponse(HttpStatus.OK, UPDATE_PROFILE.getMessage(), null, true);
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseDto<ProfileDto>> getProfile() {
        ProfileDto profileDto = profileService.getProfileByToken();
        return ResponseUtil.buildResponse(HttpStatus.OK, PROFILE_INFO.getMessage(), profileDto, true);
    }

    @GetMapping("/validateToken")
    public ResponseEntity<ResponseDto<MemberProfileDto>> validateToken(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if ((accessToken == null || accessToken.isEmpty()) && (refreshToken == null || refreshToken.isEmpty())) {
            return ResponseUtil.buildResponse(HttpStatus.UNAUTHORIZED, NOT_LOGIN.getMessage(), null, false);
        }

        TokenRequestDto tokenRequestDto = createTokenRequestDto(accessToken, refreshToken);

        TokenResultDto result = authenticationService.validateToken(tokenRequestDto);

        if (!result.isAccessTokenValid()) {
            if (result.isRefreshTokenValid()) {
                accessToken = result.getNewAccessToken().getAccessToken();
                CookieUtils.addCookie(response, "accessToken", accessToken,
                        result.getNewAccessToken().getAccessTokenExpiresIn());
            } else {
                return ResponseUtil.buildResponse(HttpStatus.UNAUTHORIZED, result.getMessage(), null, false);
            }
        }
        MemberProfileDto userInfo = profileService.getMemberInfo();
        return ResponseUtil.buildResponse(HttpStatus.OK, LOGIN_SUCCESS.getMessage(), userInfo, true);
    }

    @GetMapping("/getRecommandCafes")
    public ResponseEntity<ResponseDto<List<CafeDto>>> getRecommandCafes(
    ) {
        List<CafeDto> recommandCafes = recommendationService.getRecommendationCafes();
        return ResponseUtil.buildResponse(HttpStatus.OK, GET_RECOMMAND_CAFES.getMessage(),
                recommandCafes, true);
    }

    @PostMapping("/{cafeCode}/scrap")
    public ResponseEntity<ResponseDto<Boolean>> toggleCafeScrap(
            @PathVariable String cafeCode
    ) {
        CafeScrapDto cafeScrapDto = CafeScrapDto.builder()
                .cafeCode(cafeCode)
                .build();
        boolean isToggled = cafeScrapService.cafeScraps(cafeScrapDto);
        return ResponseUtil.buildResponse(HttpStatus.OK, POST_SCRAP_OK.getMessage(), isToggled, true);
    }

    @GetMapping("/scrapCafes")
    public ResponseEntity<ResponseDto<List<ScrapCafeCodeDto>>> getScrapCafes() {
        List<ScrapCafeCodeDto> scrapCafeCodeDtos = cafeScrapService.getCafeScrapCodes();
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.CAFESLIST_SCRAPS_OK.getMessage(),
                scrapCafeCodeDtos, true);
    }

    private String getRedirectUrlFromSession(HttpServletRequest request) {
        String redirectUrl = (String) request.getSession().getAttribute("redirectUrl");
        request.getSession().removeAttribute("redirectUrl");
        if (redirectUrl == null || !redirectUrl.startsWith("/")) {
            redirectUrl = "/";
        }
        return redirectUrl;
    }

    private AccessTokenDto createAccessTokenDto(String accessToken) {
        return AccessTokenDto.builder()
                .accessToken(accessToken)
                .build();
    }

    private RefreshTokenDto createRefreshTokenDto(String refreshToken) {
        return RefreshTokenDto.builder()
                .refreshToken(refreshToken)
                .build();
    }

    private TokenRequestDto createTokenRequestDto(String accessToken, String refreshToken) {
        return TokenRequestDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
