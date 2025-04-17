package CafeFinder.cafe.member.jwt;

import CafeFinder.cafe.member.exception.MemberNotFoundException;
import CafeFinder.cafe.member.repository.MemberRepository;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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
    @Cacheable(value = "userDetailsCache", key = "#email")
    public UserDetails loadUserByUsername(String email) {
        Optional<MemberAuthProjection> optionalProjection = memberRepository.findMemberAuthByEmail(email);
        return optionalProjection
                .map(this::createUserDetails)
                .orElseThrow(MemberNotFoundException::new);
    }

    private UserDetails createUserDetails(MemberAuthProjection projection) {
        String role = projection.getMemberRole().name();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);

        return new User(
                projection.getEmail(),
                projection.getPassword(),
                Collections.singletonList(grantedAuthority)
        );
    }
}
