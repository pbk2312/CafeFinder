package CafeFinder.cafe.member.security.jwt;

import static CafeFinder.cafe.member.security.jwt.JwtMessage.INVALID_JWT;

import CafeFinder.cafe.member.exception.InvalidTokenException;
import CafeFinder.cafe.member.exception.TokenIsExpired;
import CafeFinder.cafe.member.security.config.Jwtconfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtValidator {

    private final Jwtconfig jwtconfig;

    public boolean validate(String token) {
        try {

            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (ExpiredJwtException e) {
            throw new TokenIsExpired();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException(INVALID_JWT.getMessage());
        }


    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(
                jwtconfig.getSecrets().getAppkey().getBytes());
    }

}
