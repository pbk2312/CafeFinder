package CafeFinder.cafe.service.interfaces;

import CafeFinder.cafe.jwt.TokenDto;
import org.springframework.security.core.Authentication;

public interface TokenService {

    TokenDto generateToken(Authentication authentication);

    boolean validateToken(String token);

    Authentication getAuthentication(String token);

}
