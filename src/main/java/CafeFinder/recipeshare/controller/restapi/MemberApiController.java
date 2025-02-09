package CafeFinder.recipeshare.controller.restapi;


import static CafeFinder.recipeshare.util.ViewMessage.LOGIN_SUCCESS;
import static CafeFinder.recipeshare.util.ViewMessage.LOGOUT_SUCCESS;
import static CafeFinder.recipeshare.util.ViewMessage.SIGN_UP_SUCCESS;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import CafeFinder.recipeshare.dto.MemberLoginDto;
import CafeFinder.recipeshare.dto.MemberSignUpDto;
import CafeFinder.recipeshare.dto.ResponseDto;
import CafeFinder.recipeshare.jwt.TokenDto;
import CafeFinder.recipeshare.service.member.MemberService;
import CafeFinder.recipeshare.util.CookieUtils;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    @Value("${token.access-token-expiration}")
    private int accessTokenExpiration;
    @Value("${token.refresh-token-expiration}")
    private int refreshTokenExpiration;

    @PostMapping("/signUp")
    public ResponseEntity<ResponseDto<String>> signUp(
            @Valid @RequestBody MemberSignUpDto memberSignUpDto
    ) {
        memberService.save(memberSignUpDto);
        return ResponseEntity.ok(new ResponseDto<>(SIGN_UP_SUCCESS.getMessage(), null, true));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<String>> login(
            @Valid @RequestBody MemberLoginDto loginDto,
            HttpServletResponse response, HttpServletRequest request) {
        TokenDto tokenDto = memberService.login(loginDto);
        CookieUtils.addCookie(response, "accessToken", tokenDto.getAccessToken(), accessTokenExpiration);
        CookieUtils.addCookie(response, "refreshToken", tokenDto.getRefreshToken(), refreshTokenExpiration);

        String redirectUrl = getRedirectUrlFromSession(request);
        return ResponseEntity.ok(new ResponseDto<>(LOGIN_SUCCESS.getMessage(), redirectUrl, true));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<String>> logout(HttpServletRequest request, HttpServletResponse response) {

        // JWT 토큰 쿠키 제거
        CookieUtils.removeCookie(response, "accessToken");
        CookieUtils.removeCookie(response, "refreshToken");

        memberService.logout();

        return ResponseEntity.ok(new ResponseDto<>(LOGOUT_SUCCESS.getMessage(), null, true));
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
