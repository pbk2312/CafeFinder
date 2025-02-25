package CafeFinder.cafe.service.member;

import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.dto.MemberLoginDto;
import CafeFinder.cafe.dto.MemberSignUpDto;
import CafeFinder.cafe.dto.TokenResultDto;
import CafeFinder.cafe.jwt.AccesTokenDto;

public interface MemberService {

    // 회원가입
    void save(MemberSignUpDto memberSignUpDto);

    // 로그인
    AccesTokenDto login(MemberLoginDto memberLoginDto);

    // 로그아웃
    void logout();

    // 토큰 검증
    TokenResultDto validateToken(String accessToken);

    Member findMemberByEmail(String email);

}
