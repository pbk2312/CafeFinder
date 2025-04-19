package CafeFinder.cafe.member.service;

import CafeFinder.cafe.member.dto.TokenDto;
import CafeFinder.cafe.member.security.jwt.JwtAuthenticationProvider;
import CafeFinder.cafe.member.security.jwt.JwtValidator;
import CafeFinder.cafe.member.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationProvider authenticationProvider;
    private final JwtValidator jwtValidator;

    @Override
    public TokenDto generateToken(Authentication authentication) {
        return tokenProvider.generateTokenDto(authentication);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtValidator.validate(token);
    }

    @Override
    public Authentication getAuthentication(String token) {
        return authenticationProvider.getAuthentication(token);
    }

}
