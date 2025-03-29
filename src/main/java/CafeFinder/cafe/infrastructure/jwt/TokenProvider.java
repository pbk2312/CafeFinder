package CafeFinder.cafe.infrastructure.jwt;

import CafeFinder.cafe.exception.InvalidTokenException;
import CafeFinder.cafe.exception.MemberNotFoundException;

import static CafeFinder.cafe.infrastructure.jwt.JwtMessage.EXPIRED_JWT;
import static CafeFinder.cafe.infrastructure.jwt.JwtMessage.ILLEGAL_JWT;
import static CafeFinder.cafe.infrastructure.jwt.JwtMessage.INVALID_JWT;
import static CafeFinder.cafe.infrastructure.jwt.JwtMessage.UNSUPPORTED_JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    private final CustomUserDetailsService customUserDetailsService;

    @Value("${jwt.access-token-expire-time}") // 60분
    private long accessTokenExpireTime;

    @Value("${jwt.refresh-token-expire-time}") // 7일
    private long refreshTokenExpireTime;

    private final Key key;

    public TokenProvider(@Value("${jwt.secret}") String secretKey, CustomUserDetailsService customUserDetailsService) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.customUserDetailsService = customUserDetailsService;
    }

    public TokenDto generateTokenDto(Authentication authentication) {
        String authorities = extractAuthoritiesString(authentication);
        long now = System.currentTimeMillis();

        String accessToken = createAccessToken(authentication.getName(), authorities, now);
        String refreshToken = createRefreshToken(authentication.getName(), now);

        int accessTokenMaxAge = (int) (accessTokenExpireTime / 1000); // 밀리초 -> 초 변환
        int refreshTokenExpiresIn = (int) (refreshTokenExpireTime / 1000); // 밀리초 -> 초 변환

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenMaxAge)
                .refreshTokenExpiresIn(refreshTokenExpiresIn)
                .build();
    }

    private String createAccessToken(String subject, String authorities, long now) {
        return Jwts.builder()
                .claim(AUTHORITIES_KEY, authorities)
                .setSubject(subject)
                .signWith(key, SIGNATURE_ALGORITHM)
                .setExpiration(new Date(now + accessTokenExpireTime))
                .compact();
    }

    private String createRefreshToken(String subject, long now) {
        return Jwts.builder()
                .setSubject(subject)
                .signWith(key, SIGNATURE_ALGORITHM)
                .setExpiration(new Date(now + refreshTokenExpireTime))
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
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(claims.getSubject());
        if (userDetails == null) {
            throw new MemberNotFoundException();
        }
        return userDetails;
    }

    private static void validateClaims(Claims claims) {
        if (claims == null || claims.getSubject() == null) {
            throw new InvalidTokenException(INVALID_JWT.getMessage());
        }
    }

    private Collection<? extends GrantedAuthority> extractAuthoritiesFromClaims(Claims claims) {
        Object authoritiesClaim = claims.get(AUTHORITIES_KEY);
        if (authoritiesClaim == null) {
            throw new InvalidTokenException(INVALID_JWT.getMessage());
        }

        return Arrays.stream(authoritiesClaim.toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new InvalidTokenException(INVALID_JWT.getMessage());
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException(EXPIRED_JWT.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException(UNSUPPORTED_JWT.getMessage());
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException(ILLEGAL_JWT.getMessage());
        }
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private String extractAuthoritiesString(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

}
