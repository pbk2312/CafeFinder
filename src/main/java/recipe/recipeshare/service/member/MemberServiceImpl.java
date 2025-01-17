package recipe.recipeshare.service.member;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import recipe.recipeshare.domain.Member;
import recipe.recipeshare.dto.MemberLoginDto;
import recipe.recipeshare.dto.MemberSignUpDto;
import recipe.recipeshare.exception.IncorrectPasswordException;
import recipe.recipeshare.exception.MemberNotFoundException;
import recipe.recipeshare.jwt.TokenDto;
import recipe.recipeshare.jwt.TokenProvider;
import recipe.recipeshare.repository.MemberRepository;
import recipe.recipeshare.validator.MemberValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void save(MemberSignUpDto memberSignUpDto) {
        log.info("회원가입 시작: 이메일={}", memberSignUpDto.getEmail());

        MemberValidator.validatePassword(memberSignUpDto.getPassword(), memberSignUpDto.getCheckPassword());

        Member member = Member.create(memberSignUpDto, passwordEncoder.encode(memberSignUpDto.getPassword()));
        memberRepository.save(member);

        log.info("회원가입 성공: 이메일={}", memberSignUpDto.getEmail());
    }

    @Override
    @Transactional
    public TokenDto login(MemberLoginDto memberLoginDto) {
        log.info("로그인 시작: 이메일={}", memberLoginDto.getEmail());

        Member member = findMemberByEmail(memberLoginDto.getEmail());

        Authentication authentication = authenticateUser(memberLoginDto);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("로그인 성공: 이메일={}", memberLoginDto.getEmail());

        return generateTokenDto(authentication);
    }

    private TokenDto generateTokenDto(Authentication authentication) {
        return tokenProvider.generateTokenDto(authentication);
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("회원 조회 실패: 이메일={}", email);
                    return new MemberNotFoundException();
                });
    }

    private Authentication authenticateUser(MemberLoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken = loginDto.toAuthentication();
        try {
            return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            log.error("로그인 실패: 잘못된 비밀번호, 이메일={}", loginDto.getEmail());
            throw new IncorrectPasswordException();
        }
    }

}
