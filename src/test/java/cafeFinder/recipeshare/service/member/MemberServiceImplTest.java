package cafeFinder.recipeshare.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import CafeFinder.cafe.service.member.MemberServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.domain.MemberRole;
import CafeFinder.cafe.dto.MemberSignUpDto;
import CafeFinder.cafe.exception.PasswordMismatchException;
import CafeFinder.cafe.repository.MemberRepository;

class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private PasswordEncoder passwordEncoder;  // 추가됨

    @InjectMocks
    private MemberServiceImpl memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(any(String.class))).thenReturn("true");  // 이메일 인증 성공 가정
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword123"); // 패스워드 암호화 Mock 설정
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void testSaveMember() {

        // given
        MemberSignUpDto memberSignUpDto = new MemberSignUpDto();
        memberSignUpDto.setNickName("하이루");
        memberSignUpDto.setEmail("하이루@example.com");
        memberSignUpDto.setPassword("password123!");
        memberSignUpDto.setCheckPassword("password123!");

        Member mockMember = new Member(null, "하이루", "하이루@example.com", "encodedPassword123", MemberRole.REGULAR);

        // mock 설정
        when(memberRepository.save(any(Member.class))).thenReturn(mockMember);
        when(memberRepository.findByEmail(memberSignUpDto.getEmail())).thenReturn(Optional.of(mockMember));

        // when
        memberService.save(memberSignUpDto);

        // then
        Member member = memberRepository.findByEmail(memberSignUpDto.getEmail()).orElseThrow();
        assertThat(member.getEmail()).isEqualTo(memberSignUpDto.getEmail());
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 비밀번호 불일치")
    void testSaveInvalidPassword() {
        // given
        MemberSignUpDto memberSignUpDto = new MemberSignUpDto();
        memberSignUpDto.setNickName("하이루");
        memberSignUpDto.setEmail("하이루@example.com");
        memberSignUpDto.setPassword("kkkkkkkkk!");
        memberSignUpDto.setCheckPassword("password123!");

        // when, then
        org.junit.jupiter.api.Assertions.assertThrows(PasswordMismatchException.class, () -> {
            memberService.save(memberSignUpDto);
        });
    }
}
