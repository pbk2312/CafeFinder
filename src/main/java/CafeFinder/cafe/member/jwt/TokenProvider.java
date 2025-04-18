package CafeFinder.cafe.member.jwt;

import static CafeFinder.cafe.member.jwt.JwtMessage.INVALID_JWT;

import CafeFinder.cafe.member.dto.TokenDto;
import CafeFinder.cafe.member.exception.InvalidTokenException;
import CafeFinder.cafe.member.exception.MemberNotFoundException;
import CafeFinder.cafe.member.jwt.config.Jwtconfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {


    private final Jwtconfig jwtconfig;

    private final CustomUserDetailsService customUserDetailsService;


    public TokenDto generateTokenDto(Authentication authentication) {
        String authorities = extractAuthoritiesString(authentication);

        String accessToken = createAccessToken(authentication.getName(), authorities);
        String refreshToken = createRefreshToken(authentication.getName());

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(jwtconfig.getValidation().getAccess().intValue())
                .refreshTokenExpiresIn(jwtconfig.getValidation().getRefresh().intValue())
                .build();
    }

    private String createAccessToken(String email, String authorities) {
        return Jwts.builder()
                .claim("email", email)
                .claim("role", authorities)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtconfig.getValidation().getAccess()))
                .signWith(getSecretKey(), SIG.HS256)
                .compact();
    }

    private String createRefreshToken(String email) {
        return Jwts.builder()
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtconfig.getValidation().getRefresh()))
                .signWith(getSecretKey(), SIG.HS256)
                .compact();
    }


    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        UserDetails userDetails = getUserDetailsFromClaims(claims);
        Collection<? extends GrantedAuthority> authorities = extractAuthoritiesFromClaims(claims);

        return new UsernamePasswordAuthenticationToken(userDetails, accessToken, authorities);
    }


    public UserDetails getUserDetailsFromRefreshToken(String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        return getUserDetailsFromClaims(claims);
    }

    private UserDetails getUserDetailsFromClaims(Claims claims) {
        validateClaims(claims);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(claims.get("email").toString());
        if (userDetails == null) {
            throw new MemberNotFoundException();
        }
        return userDetails;
    }

    private static void validateClaims(Claims claims) {
        if (claims == null) {
            throw new InvalidTokenException(INVALID_JWT.getMessage());
        }
    }


    private Collection<? extends GrantedAuthority> extractAuthoritiesFromClaims(Claims claims) {
        if (claims == null) {
            throw new InvalidTokenException(INVALID_JWT.getMessage());
        }

        String role = claims.get("role", String.class);
        if (role == null || role.isBlank()) {
            return Collections.emptyList();
        }

        return List.of(new SimpleGrantedAuthority(role));
    }


    public boolean validate(String token) {
        try {

            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (JwtException e) {
            throw new InvalidTokenException(INVALID_JWT.getMessage());
        }

    }

    private Claims parseClaims(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);

            return claimsJws.getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private String extractAuthoritiesString(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(
                jwtconfig.getSecrets().getAppkey().getBytes());
    }

}
