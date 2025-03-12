package CafeFinder.cafe.controller.restapi;

import CafeFinder.cafe.dto.MemberLoginDto;
import CafeFinder.cafe.dto.MemberSignUpDto;
import CafeFinder.cafe.dto.ProfileDto;
import CafeFinder.cafe.dto.ResponseDto;
import CafeFinder.cafe.dto.TokenResultDto;
import CafeFinder.cafe.dto.UserInfoDto;
import CafeFinder.cafe.dto.UserUpdateDto;
import CafeFinder.cafe.jwt.AccesTokenDto;
import CafeFinder.cafe.service.interfaces.MemberService;
import CafeFinder.cafe.util.CookieUtils;
import CafeFinder.cafe.util.ResponseUtil;
import static CafeFinder.cafe.util.ViewMessage.LOGIN_SUCCESS;
import static CafeFinder.cafe.util.ViewMessage.LOGOUT_SUCCESS;
import static CafeFinder.cafe.util.ViewMessage.NOT_LOGIN;
import static CafeFinder.cafe.util.ViewMessage.PROFILE_INFO;
import static CafeFinder.cafe.util.ViewMessage.SIGN_UP_SUCCESS;
import static CafeFinder.cafe.util.ViewMessage.UPDATE_PROFILE;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/signUp")
    public ResponseEntity<ResponseDto<String>> signUp(
            @Valid @ModelAttribute MemberSignUpDto memberSignUpDto
    ) {
        memberService.save(memberSignUpDto);
        return ResponseUtil.buildResponse(HttpStatus.OK, SIGN_UP_SUCCESS.getMessage(), null, true);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<String>> login(
            @Valid @RequestBody MemberLoginDto loginDto,
            HttpServletResponse response, HttpServletRequest request) {
        AccesTokenDto accesTokenDto = memberService.login(loginDto);
        CookieUtils.addCookie(response, "accessToken", accesTokenDto.getAccessToken(),
                accesTokenDto.getAccessTokenExpiresIn());
        String redirectUrl = getRedirectUrlFromSession(request);
        return ResponseUtil.buildResponse(HttpStatus.OK, LOGIN_SUCCESS.getMessage(), redirectUrl, true);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<String>> logout(HttpServletResponse response) {
        CookieUtils.removeCookie(response, "accessToken");
        memberService.logout();
        return ResponseUtil.buildResponse(HttpStatus.OK, LOGOUT_SUCCESS.getMessage(), null, true);
    }

    @PatchMapping("/update")
    public ResponseEntity<ResponseDto<String>> update(
            @Valid UserUpdateDto userUpdateDto,
            @CookieValue(value = "accessToken", required = false) String accessToken
    ) {
        memberService.update(userUpdateDto, accessToken);
        return ResponseUtil.buildResponse(HttpStatus.OK, UPDATE_PROFILE.getMessage(), null, true);
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseDto<ProfileDto>> getProfile(
            @CookieValue(value = "accessToken", required = true) String accessToen) {
        ProfileDto profileDto = memberService.getProfileByToken(accessToen);
        return ResponseUtil.buildResponse(HttpStatus.OK, PROFILE_INFO.getMessage(), profileDto, true);
    }

    @GetMapping("/validateToken")
    public ResponseEntity<ResponseDto<UserInfoDto>> validateToken(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            HttpServletResponse response
    ) {
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseUtil.buildResponse(HttpStatus.OK, NOT_LOGIN.getMessage(), null, false);
        }

        TokenResultDto result = memberService.validateToken(accessToken);

        if (!result.isAccessTokenValid()) {
            if (result.isRefreshTokenValid()) {
                accessToken = result.getNewAccessToken().getAccessToken();
                CookieUtils.addCookie(response, "accessToken", accessToken,
                        result.getNewAccessToken().getAccessTokenExpiresIn());
            } else {
                return ResponseUtil.buildResponse(HttpStatus.OK, result.getMessage(), null, false);
            }
        }

        UserInfoDto userInfo = memberService.getUserInfoByToken(accessToken);
        return ResponseUtil.buildResponse(HttpStatus.OK, LOGIN_SUCCESS.getMessage(), userInfo, true);
    }

    private String getRedirectUrlFromSession(HttpServletRequest request) {
        String redirectUrl = (String) request.getSession().getAttribute("redirectUrl");
        request.getSession().removeAttribute("redirectUrl");
        if (redirectUrl == null || !redirectUrl.startsWith("/")) {
            redirectUrl = "/";
        }
        return redirectUrl;
    }

}
