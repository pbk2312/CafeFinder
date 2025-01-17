package recipe.recipeshare.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import recipe.recipeshare.domain.Member;
import recipe.recipeshare.dto.MemberSignUpDto;
import recipe.recipeshare.exception.PasswordMismatchException;
import recipe.recipeshare.repository.MemberRepository;

class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;


    @InjectMocks
    private MemberServiceImpl memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

        // mock
        Member mockMember = new Member(null, "하이루", "하이루@example.com", "password123!", null);
        when(memberRepository.save(mockMember)).thenReturn(mockMember);
        when(memberRepository.findByEmail(memberSignUpDto.getEmail())).thenReturn(java.util.Optional.of(mockMember));

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

        // mock
        Member mockMember = new Member(null, "하이루", "하이루@example.com", "password123!", null);
        when(memberRepository.save(mockMember)).thenReturn(mockMember);
        when(memberRepository.findByEmail(memberSignUpDto.getEmail())).thenReturn(java.util.Optional.of(mockMember));

        // when, then
        Assertions.assertThrows(PasswordMismatchException.class, () -> {
            memberService.save(memberSignUpDto);
        });
    }


}
