package CafeFinder.cafe.member.service;

import CafeFinder.cafe.member.dto.AccesTokenDto;
import CafeFinder.cafe.member.dto.RefreshTokenDto;
import CafeFinder.cafe.member.dto.TokenDto;
import org.springframework.security.core.Authentication;

public interface TokenService {

    TokenDto generateToken(Authentication authentication);

    boolean validateToken(String token);

    AccesTokenDto reIssueToken(RefreshTokenDto refreshTokenDto);


}
