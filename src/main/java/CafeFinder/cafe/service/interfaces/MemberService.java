package CafeFinder.cafe.service.interfaces;

import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.dto.MemberLoginDto;
import CafeFinder.cafe.dto.MemberSignUpDto;
import CafeFinder.cafe.dto.ProfileDto;
import CafeFinder.cafe.dto.TokenResultDto;
import CafeFinder.cafe.dto.UserInfoDto;
import CafeFinder.cafe.dto.UserUpdateDto;
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

    // 이메일로 회원 찾기
    Member getMemberByEmail(String email);

    // 회원 정보 가져오기
    UserInfoDto getUserInfoByToken(String accessToken);

    // 회원 정보 수정
    void update(UserUpdateDto userUpdateDto, String accessToken);

    // 회원 정보 보기
    ProfileDto getProfileByToken(String accessToken);

    // 토큰으로 멤버 찾기
    Member getMemberByToken(String accessToken);

}
