package CafeFinder.cafe.infrastructure.jwt;


import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.exception.MemberNotFoundException;
import CafeFinder.cafe.repository.MemberRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

// 사용자 인증 정보를 로드
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return memberRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(MemberNotFoundException::new);
    }

    private UserDetails createUserDetails(Member member) {
        String role = member.getMemberRole().toString();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);

        return new User(
                member.getEmail(),
                member.getPassword(),
                Collections.singletonList(grantedAuthority)
        );
    }

}

