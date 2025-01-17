package recipe.recipeshare.controller.restapi;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import recipe.recipeshare.dto.MemberLoginDto;
import recipe.recipeshare.dto.MemberSignUpDto;
import recipe.recipeshare.dto.ResponseDto;
import recipe.recipeshare.jwt.TokenDto;
import recipe.recipeshare.service.member.MemberService;
import recipe.recipeshare.util.CookieUtils;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberApiController {

    private static final String SIGN_UP_SUCCESS = "회원가입 성공";
    private static final String LOGIN_SUCCESS = "로그인 성공";
    private static final int ACCESS_TOKEN_EXPIRATION = 60 * 60; // 1시간
    private static final int REFRESH_TOKEN_EXPIRATION = 60 * 60 * 24 * 7; // 7일

    private final MemberService memberService;


    @PostMapping("/signUp")
    public ResponseEntity<ResponseDto<String>> signUp(
            @Valid
            @RequestBody MemberSignUpDto memberSignUpDto
    ) {
        memberService.save(memberSignUpDto);
        return ResponseEntity.ok(new ResponseDto<>(SIGN_UP_SUCCESS, null, true));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<String>> login(
            @RequestBody MemberLoginDto loginDto, HttpServletResponse response, HttpServletRequest request) {
        TokenDto tokenDto = memberService.login(loginDto);
        CookieUtils.addCookie(response, "accessToken", tokenDto.getAccessToken(), ACCESS_TOKEN_EXPIRATION);
        CookieUtils.addCookie(response, "refreshToken", tokenDto.getRefreshToken(), REFRESH_TOKEN_EXPIRATION);
        String redirectUrl = getRedirectUrlFromSession(request);
        return ResponseEntity.ok(new ResponseDto<>(LOGIN_SUCCESS, redirectUrl, true));
    }

    private String getRedirectUrlFromSession(HttpServletRequest request) {
        String redirectUrl = (String) request.getSession().getAttribute("redirectUrl");
        request.getSession().removeAttribute("redirectUrl");
        return redirectUrl != null ? redirectUrl : "/";
    }

}
