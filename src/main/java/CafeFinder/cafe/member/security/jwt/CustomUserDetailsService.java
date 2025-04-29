package CafeFinder.cafe.member.security.jwt;

import static CafeFinder.cafe.global.exception.ErrorCode.MEMBER_NOT_FOUND;

import CafeFinder.cafe.global.exception.ErrorException;
import CafeFinder.cafe.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        Optional<MemberAuthProjection> optionalProjection = memberRepository.findMemberAuthByEmail(
            email);
        return optionalProjection
            .map(this::createUserDetails)
            .orElseThrow(() -> new ErrorException(MEMBER_NOT_FOUND));
    }

    private UserDetails createUserDetails(MemberAuthProjection projection) {
        return CustomUserDetails.builder()
            .nickName(projection.getNickName())
            .password(projection.getPassword())
            .memberId(projection.getId())
            .memberRole(projection.getMemberRole())
            .username(projection.getEmail())
            .build();
    }

}
