package CafeFinder.cafe.controller.restapi;


import static CafeFinder.cafe.util.ViewMessage.LOGIN_SUCCESS;
import static CafeFinder.cafe.util.ViewMessage.LOGOUT_SUCCESS;
import static CafeFinder.cafe.util.ViewMessage.NOT_LOGIN;
import static CafeFinder.cafe.util.ViewMessage.SIGN_UP_SUCCESS;

import CafeFinder.cafe.dto.MemberLoginDto;
import CafeFinder.cafe.dto.MemberSignUpDto;
import CafeFinder.cafe.dto.ResponseDto;
import CafeFinder.cafe.dto.TokenResultDto;
import CafeFinder.cafe.dto.UserInfoDto;
import CafeFinder.cafe.jwt.AccesTokenDto;
import CafeFinder.cafe.service.member.MemberService;
import CafeFinder.cafe.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
        return ResponseEntity.ok(new ResponseDto<>(SIGN_UP_SUCCESS.getMessage(), null, true));
    }


    @PostMapping("/login")
    public ResponseEntity<ResponseDto<String>> login(
            @Valid @RequestBody MemberLoginDto loginDto,
            HttpServletResponse response, HttpServletRequest request) {
        AccesTokenDto accesTokenDto = memberService.login(loginDto);
        CookieUtils.addCookie(response, "accessToken", accesTokenDto.getAccessToken(),
                accesTokenDto.getAccessTokenExpiresIn());
        String redirectUrl = getRedirectUrlFromSession(request);
        return ResponseEntity.ok(new ResponseDto<>(LOGIN_SUCCESS.getMessage(), redirectUrl, true));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<String>> logout(HttpServletResponse response) {
        // JWT 토큰 쿠키 제거
        CookieUtils.removeCookie(response, "accessToken");
        memberService.logout();

        return ResponseEntity.ok(new ResponseDto<>(LOGOUT_SUCCESS.getMessage(), null, true));
    }

    @GetMapping("/validateToken")
    public ResponseEntity<ResponseDto<UserInfoDto>> validateToken(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            HttpServletResponse response
    ) {

        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.ok(new ResponseDto<>(NOT_LOGIN.getMessage(), null, false));
        }

        TokenResultDto result = memberService.validateToken(accessToken);

        if (!result.isAccessTokenValid()) {
            if (result.isRefreshTokenValid()) {
                accessToken = result.getNewAccessToken().getAccessToken();
                CookieUtils.addCookie(response, "accessToken", accessToken,
                        result.getNewAccessToken().getAccessTokenExpiresIn());
            } else {
                return ResponseEntity.ok(new ResponseDto<>(result.getMessage(), null, false));
            }
        }

        // accessToken을 사용하여 사용자 정보 조회
        UserInfoDto userInfo = memberService.findUserInfoByToken(accessToken);

        return ResponseEntity.ok(new ResponseDto<>("로그인 성공", userInfo, true));
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
