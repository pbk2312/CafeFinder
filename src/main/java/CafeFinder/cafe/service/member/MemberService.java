package CafeFinder.cafe.service.member;

import CafeFinder.cafe.dto.MemberLoginDto;
import CafeFinder.cafe.dto.MemberSignUpDto;
import CafeFinder.cafe.jwt.TokenDto;

public interface MemberService {

    // 회원가입
    void save(MemberSignUpDto memberSignUpDto);

    // 로그인
    TokenDto login(MemberLoginDto memberLoginDto);

    // 로그아웃
    void logout();
}
