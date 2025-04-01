package CafeFinder.cafe.service.interfaces;

import CafeFinder.cafe.dto.MemberLoginDto;
import CafeFinder.cafe.dto.TokenResultDto;
import CafeFinder.cafe.infrastructure.jwt.TokenDto;

public interface AuthenticationService {
    
    TokenDto login(MemberLoginDto memberLoginDto);

    void logout(String refreshToken);

    TokenResultDto validateToken(String accessToken, String refreshToken);

}
