package recipe.recipeshare.jwt;


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
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import recipe.recipeshare.exception.InvalidTokenException;
import recipe.recipeshare.exception.MemberNotFoundException;

@Log4j2
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
    private static final String INVALID_JWT = "유효하지 않은 JWT 토큰입니다.";
    private static final String EXPIRED_JWT = "만료된 JWT 토큰입니다.";
    private static final String UNSUPPORTED_JWT = "지원하지 않는 JWT 토큰입니다.";
    private static final String ILLEGAL_JWT = "잘못된 JWT 토큰입니다.";


    private final CustomUserDetailsService customUserDetailsService;

    @Value("${jwt.access-token-expire-time}")
    private long accessTokenExpireTime;

    @Value("${jwt.refresh-token-expire-time}")
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

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(now + accessTokenExpireTime)
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

    public Authentication getAuthenticationFromRefreshToken(String refreshToken) {

        // 리프레시 토큰 검증
        validate(refreshToken);

        Claims claims = parseClaims(refreshToken);
        UserDetails userDetails = getUserDetailsFromClaims(claims);

        // 새 액세스 토큰 생성
        String newAccessToken = createAccessToken(
                claims.getSubject(),
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")),
                System.currentTimeMillis()
        );

        return new UsernamePasswordAuthenticationToken(userDetails, newAccessToken, userDetails.getAuthorities());
    }

    private UserDetails getUserDetailsFromClaims(Claims claims) {
        if (claims == null || claims.getSubject() == null) {
            throw new InvalidTokenException(INVALID_JWT);
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(claims.getSubject());
        if (userDetails == null) {
            throw new MemberNotFoundException();
        }

        return userDetails;
    }

    private Collection<? extends GrantedAuthority> extractAuthoritiesFromClaims(Claims claims) {
        Object authoritiesClaim = claims.get(AUTHORITIES_KEY);
        if (authoritiesClaim == null) {
            throw new InvalidTokenException(INVALID_JWT);
        }

        return Arrays.stream(authoritiesClaim.toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new InvalidTokenException(INVALID_JWT);
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException(EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException(UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException(ILLEGAL_JWT);
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

