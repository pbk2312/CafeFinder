package CafeFinder.cafe.infrastructure.auth;

import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.infrastructure.auth.AuthProvider.OAuth2MemberFactory;
import CafeFinder.cafe.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2MemberService extends DefaultOAuth2UserService {


    @Value("${file.default.image}")
    private String defaultProfileImage;

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. OAuth2 로그인 유저 정보를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        // 2. provider : kakao, naver, google
        String provider = userRequest.getClientRegistration().getRegistrationId();
        log.info("provider : {}", provider);

        // 3. 필요한 정보를 provider에 따라 다르게 mapping
        OAuth2Member oAuth2UserInfo = OAuth2MemberFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());
        log.info("oAuth2UserInfo : {}", oAuth2UserInfo.toString());

        // 4. oAuth2UserInfo가 저장되어 있는지 유저 정보 확인
        //    없으면 DB 저장 후 해당 유저를 저장
        //    있으면 해당 유저를 저장
        Member member = memberRepository.findByEmail(oAuth2UserInfo.getEmail())
                .orElseGet(() -> memberRepository.save(oAuth2UserInfo.toEntity(defaultProfileImage)));
        log.info("user : {}", member.toString());

        // 5. UserDetails와 OAuth2User를 다중 상속한 CustomUserDetails
        return new CustomUserDetails(member, oAuth2User.getAttributes());
    }

}
