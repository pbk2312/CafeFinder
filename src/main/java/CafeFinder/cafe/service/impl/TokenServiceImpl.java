package CafeFinder.cafe.service.impl;

import CafeFinder.cafe.infrastructure.jwt.TokenDto;
import CafeFinder.cafe.infrastructure.jwt.TokenProvider;
import CafeFinder.cafe.service.interfaces.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
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
