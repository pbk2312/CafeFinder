package CafeFinder.cafe.member.service;

import CafeFinder.cafe.member.dto.MemberLoginDto;
import CafeFinder.cafe.member.dto.RefreshTokenDto;
import CafeFinder.cafe.member.dto.TokenRequestDto;
import CafeFinder.cafe.member.dto.TokenResultDto;
import CafeFinder.cafe.member.dto.TokenDto;

public interface AuthenticationService {

    TokenDto login(MemberLoginDto memberLoginDto);

    void logout(RefreshTokenDto refreshTokenDto);

    TokenResultDto validateToken(TokenRequestDto tokenRequestDto);

}
