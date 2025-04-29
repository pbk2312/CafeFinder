package CafeFinder.cafe.member.controller;

import static CafeFinder.cafe.global.util.ResponseMessage.LOGIN_SUCCESS;
import static CafeFinder.cafe.global.util.ResponseMessage.NOT_LOGIN;
import static CafeFinder.cafe.global.util.ResponseMessage.REISSUE_TOKEN_SUCCESS;

import CafeFinder.cafe.global.dto.ResponseDto;
import CafeFinder.cafe.global.util.ResponseUtil;
import CafeFinder.cafe.member.dto.AccesTokenDto;
import CafeFinder.cafe.member.dto.MemberProfileDto;
import CafeFinder.cafe.member.dto.RefreshTokenDto;
import CafeFinder.cafe.member.dto.TokenRequestDto;
import CafeFinder.cafe.member.dto.TokenResultDto;
import CafeFinder.cafe.member.service.AuthenticationService;
import CafeFinder.cafe.member.service.ProfileService;
import CafeFinder.cafe.member.service.TokenService;
import CafeFinder.cafe.member.util.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/token")
public class TokenApiController {

    private final TokenService tokenService;
    private final ProfileService profileService;
    private final AuthenticationService authenticationService;

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

    @GetMapping("/refreshToken")
    public ResponseEntity<ResponseDto<AccesTokenDto>> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        RefreshTokenDto refreshTokenDto = createRefreshTokenDto(refreshToken);
        AccesTokenDto accesTokenDto = tokenService.reIssueToken(refreshTokenDto);
        if (accesTokenDto == null) {
            CookieUtils.removeCookie(response, "refreshToken");
            CookieUtils.removeCookie(response, "accessToken");
            return ResponseUtil.buildResponse(HttpStatus.UNAUTHORIZED, NOT_LOGIN.getMessage(), null, false);
        }

        CookieUtils.addCookie(response, "accessToken", accesTokenDto.getAccessToken(),
                accesTokenDto.getAccessTokenExpiresIn());
        return ResponseUtil.buildResponse(HttpStatus.OK, REISSUE_TOKEN_SUCCESS.getMessage(),
                accesTokenDto, true);
    }

    private TokenRequestDto createTokenRequestDto(String accessToken, String refreshToken) {
        return TokenRequestDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private RefreshTokenDto createRefreshTokenDto(String refreshToken) {
        return RefreshTokenDto.builder()
                .refreshToken(refreshToken)
                .build();
    }

}
