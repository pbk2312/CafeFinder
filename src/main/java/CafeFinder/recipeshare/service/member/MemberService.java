package CafeFinder.recipeshare.service.member;

import CafeFinder.recipeshare.dto.MemberLoginDto;
import CafeFinder.recipeshare.dto.MemberSignUpDto;
import CafeFinder.recipeshare.jwt.TokenDto;

public interface MemberService {

    // 회원가입
    void save(MemberSignUpDto memberSignUpDto);

    // 로그인
    TokenDto login(MemberLoginDto memberLoginDto);

    // 로그아웃
    void logout();
}
