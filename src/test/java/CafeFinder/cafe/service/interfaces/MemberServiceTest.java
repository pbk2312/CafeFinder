package CafeFinder.cafe.service.interfaces;

import CafeFinder.cafe.member.dto.MemberSignUpDto;
import CafeFinder.cafe.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    public void testSaveDummyMembers() {
        for (int i = 1; i <= 500; i++) {
            MemberSignUpDto dummy = MemberSignUpDto.builder()
                    .nickName("DummyUser" + i)
                    .email("dummyuser" + i + "@example.com")
                    .password("Test@1234")
                    .checkPassword("Test@1234")
                    .profileImage(null)
                    .build();

            memberService.save(dummy);
        }
    }

}
