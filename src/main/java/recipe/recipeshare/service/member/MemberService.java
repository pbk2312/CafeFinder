package recipe.recipeshare.service.member;

import recipe.recipeshare.dto.MemberLoginDto;
import recipe.recipeshare.dto.MemberSignUpDto;
import recipe.recipeshare.jwt.TokenDto;

public interface MemberService {

    // 회원가입
    void save(MemberSignUpDto memberSignUpDto);

    // 로그인
    TokenDto login(MemberLoginDto memberLoginDto);

}
