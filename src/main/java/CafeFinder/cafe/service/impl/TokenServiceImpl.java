package CafeFinder.cafe.service.impl;

import CafeFinder.cafe.jwt.TokenDto;
import CafeFinder.cafe.jwt.TokenProvider;
import CafeFinder.cafe.service.interfaces.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class TokenServiceImpl implements TokenService {

    private final TokenProvider tokenProvider;

    @Override
    public TokenDto generateToken(Authentication authentication) {
        return tokenProvider.generateTokenDto(authentication);
    }

    @Override
    public boolean validateToken(String token) {
        return tokenProvider.validate(token);
    }

    @Override
    public Authentication getAuthentication(String token) {
        return tokenProvider.getAuthentication(token);
    }

}
