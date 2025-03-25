package CafeFinder.cafe.service.interfaces;

import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.dto.MemberLoginDto;
import CafeFinder.cafe.dto.MemberProfileDto;
import CafeFinder.cafe.dto.MemberSignUpDto;
import CafeFinder.cafe.dto.MemberUpdateDto;
import CafeFinder.cafe.dto.ProfileDto;
import CafeFinder.cafe.dto.TokenResultDto;
import CafeFinder.cafe.infrastructure.jwt.TokenDto;

public interface MemberService {

    void save(MemberSignUpDto memberSignUpDto);

    TokenDto login(MemberLoginDto memberLoginDto);

    void logout(String refreshToken);

    TokenResultDto validateToken(String accessToken, String refreshToken);

    Member getMemberByEmail(String email);

    MemberProfileDto getUserInfoByToken(String accessToken);

    void update(MemberUpdateDto userUpdateDto, String accessToken);

    ProfileDto getProfileByToken(String accessToken);

    Member getMemberByToken(String accessToken);

}
