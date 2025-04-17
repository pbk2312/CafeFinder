package CafeFinder.cafe.member.service;

import CafeFinder.cafe.member.dto.TokenDto;
import org.springframework.security.core.Authentication;

public interface TokenService {

    TokenDto generateToken(Authentication authentication);

    boolean validateToken(String token);

    Authentication getAuthentication(String token);

}
