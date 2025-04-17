package CafeFinder.cafe.member.service;

import CafeFinder.cafe.member.domain.Member;
import CafeFinder.cafe.member.dto.MemberSignUpDto;
import CafeFinder.cafe.member.exception.MemberNotFoundException;
import CafeFinder.cafe.member.exception.YetVerifyEmailException;
import CafeFinder.cafe.global.infrastructure.redis.RedisEmailVerifyService;
import CafeFinder.cafe.member.repository.MemberRepository;
import CafeFinder.cafe.global.service.FileService;
import CafeFinder.cafe.member.validator.MemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {


    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisEmailVerifyService redisEmailVerifyService;
    private final FileService fileService;

    @Override
    @Transactional
    public void save(MemberSignUpDto signUpDto) {
        log.info("회원가입 시작: 이메일={}", signUpDto.getEmail());
        verifyEmailAuthentication(signUpDto);
        MemberValidator.validatePassword(signUpDto.getPassword(), signUpDto.getCheckPassword());
        String profileImagePath = fileService.saveProfileImage(signUpDto.getProfileImage());
        Member member = Member.create(signUpDto, passwordEncoder.encode(signUpDto.getPassword()), profileImagePath);
        memberRepository.save(member);
        redisEmailVerifyService.deleteVerificationCode(signUpDto.getEmail());
        log.info("회원가입 성공: 이메일={}", signUpDto.getEmail());
    }


    @Override
    public Member getMemberByToken(String accessToken) {
        Authentication authentication = tokenService.getAuthentication(accessToken);
        return getMemberFromAuthentication(authentication);
    }

    @Override
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("회원 조회 실패: 이메일={}", email);
                    return new MemberNotFoundException();
                });
    }

    private void verifyEmailAuthentication(MemberSignUpDto signUpDto) {
        String isVerified = redisTemplate.opsForValue().get("verified:" + signUpDto.getEmail());
        if (!"true".equals(isVerified)) {
            log.warn("회원가입 실패: 이메일 인증 미완료, 이메일={}", signUpDto.getEmail());
            throw new YetVerifyEmailException();
        }
    }

    private Member getMemberFromAuthentication(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return getMemberByEmail(userDetails.getUsername());
        } catch (Exception e) {
            log.warn("인증된 사용자를 찾을 수 없음: authentication={}", authentication);
            throw new MemberNotFoundException();
        }
    }

}

