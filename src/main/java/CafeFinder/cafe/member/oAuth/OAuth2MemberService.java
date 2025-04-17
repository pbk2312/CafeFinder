package CafeFinder.cafe.member.oAuth;

import static CafeFinder.cafe.global.util.ErrorMessage.DUPLICARTED_SOCAIL;

import CafeFinder.cafe.member.domain.AuthProvider;
import CafeFinder.cafe.member.domain.Member;
import CafeFinder.cafe.member.domain.MemberRole;
import CafeFinder.cafe.member.repository.MemberRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2MemberService extends DefaultOAuth2UserService {


    @Value("${file.default.image}")
    private String defaultProfileImage;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String providerId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        MemberDetails memberDetails = MemberDetailsFactory.memberDetails(providerId, oAuth2User);

        Optional<Member> memberOptional = memberRepository.findByEmail(memberDetails.getEmail());

        String rawUuid = UUID.randomUUID().toString();
        String encodedPwd = passwordEncoder.encode(rawUuid);

        Member findMember = memberOptional.orElseGet(() -> {
                    Member member = Member.builder()
                            .email(memberDetails.getEmail())
                            .provider(AuthProvider.valueOf(providerId))
                            .nickName(memberDetails.getName())
                            .password(encodedPwd)
                            .memberRole(MemberRole.REGULAR)
                            .profileImagePath(defaultProfileImage)
                            .build();
                    return memberRepository.save(member);
                }
        );

        if (findMember.getProvider().equals(AuthProvider.valueOf(providerId))) {
            return memberDetails.setMemberRole(findMember.getMemberRole());
        } else {
            throw new IllegalStateException(DUPLICARTED_SOCAIL.getMessage());
        }

    }

}
