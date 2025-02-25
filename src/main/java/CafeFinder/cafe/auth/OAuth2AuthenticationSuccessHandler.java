package CafeFinder.cafe.auth;

import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.exception.MemberNotFoundException;
import CafeFinder.cafe.jwt.TokenDto;
import CafeFinder.cafe.jwt.TokenProvider;
import CafeFinder.cafe.service.member.MemberService;
import CafeFinder.cafe.service.redis.RefreshTokenService;
import CafeFinder.cafe.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Log4j2
@SuppressWarnings("unchecked")
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private MemberService memberService;
    private TokenProvider tokenProvider;
    private RefreshTokenService refreshTokenService;


    public OAuth2AuthenticationSuccessHandler(MemberService memberService, TokenProvider tokenProvider,
                                              RefreshTokenService refreshTokenService) {
        this.memberService = memberService;
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenService;
    }


    public OAuth2AuthenticationSuccessHandler(String defaultTargetUrl, MemberService memberService,
                                              TokenProvider tokenProvider, RefreshTokenService refreshTokenService) {
        super(defaultTargetUrl);
        this.memberService = memberService;
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("OAuth2 로그인 성공: {}", authentication.getPrincipal());

        // 1. OAuth2User 정보 가져오기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 2. 이메일 추출
        String email = extractEmail(oAuth2User);

        // 3. Member 조회 또는 생성
        Member member = memberService.findMemberByEmail(email);

        // 4. UsernamePasswordAuthenticationToken 생성
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                member.getEmail(),
                member.getPassword(),
                authentication.getAuthorities()
        );

        // 5. JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(newAuthentication);

        // 6. Refresh Token Redis에 저장
        refreshTokenService.saveRefreshToken(member.getId(), tokenDto.getRefreshToken(),
                tokenDto.getRefreshTokenExpiresIn());

        // 7. Access Token과 Refresh Token을 쿠키에 저장
        CookieUtils.addCookie(response, "accessToken", tokenDto.getAccessToken(), tokenDto.getAccessTokenExpiresIn());

        // 8. 홈 페이지로 리다이렉트 설정
        String targetUrl = "/";  // 홈 페이지 URL
        log.info("리다이렉트 URL: {}", targetUrl);

        // 9. 리다이렉트 처리
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * OAuth2 제공자에 따라 이메일을 추출하는 메서드
     */
    private String extractEmail(OAuth2User oAuth2User) {
        String email = extractEmailFromKakao(oAuth2User);
        if (email == null) {
            email = extractEmailFromNaver(oAuth2User);
        }
        if (email == null) {
            email = extractGenericEmail(oAuth2User);
        }
        if (email == null) {
            throw new MemberNotFoundException();
        }
        return email;
    }

    private String extractEmailFromKakao(OAuth2User oAuth2User) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttribute("kakao_account");
        return kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
    }

    private String extractEmailFromNaver(OAuth2User oAuth2User) {
        Map<String, Object> naverResponse = (Map<String, Object>) oAuth2User.getAttribute("response");
        return naverResponse != null ? (String) naverResponse.get("email") : null;
    }

    private String extractGenericEmail(OAuth2User oAuth2User) {
        return oAuth2User.getAttribute("email");
    }

}
