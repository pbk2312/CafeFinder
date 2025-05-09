package CafeFinder.cafe.member.security.jwt;


import static CafeFinder.cafe.global.exception.ErrorCode.INVALID_JWT;
import static CafeFinder.cafe.global.exception.ErrorCode.MEMBER_NOT_FOUND;

import CafeFinder.cafe.global.exception.ErrorException;
import io.jsonwebtoken.Claims;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider {

    private final JwtClaimsProvider claimsProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public Authentication getAuthentication(String accessToken) {
        Claims claims = claimsProvider.getClaims(accessToken);
        UserDetails userDetails = getUserDetailsFromClaims(claims);
        Collection<? extends GrantedAuthority> authorities = extractAuthoritiesFromClaims(claims);

        return new UsernamePasswordAuthenticationToken(userDetails, accessToken, authorities);
    }


    private UserDetails getUserDetailsFromClaims(Claims claims) {
        validateClaims(claims);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(
            claims.get("email").toString());
        if (userDetails == null) {
            throw new ErrorException(MEMBER_NOT_FOUND);
        }
        return userDetails;
    }

    private static void validateClaims(Claims claims) {
        if (claims == null) {
            throw new ErrorException(INVALID_JWT);
        }
    }

    private Collection<? extends GrantedAuthority> extractAuthoritiesFromClaims(Claims claims) {
        if (claims == null) {
            throw new ErrorException(INVALID_JWT);
        }

        String role = claims.get("role", String.class);
        if (role == null || role.isBlank()) {
            return Collections.emptyList();
        }

        return List.of(new SimpleGrantedAuthority(role));
    }

    public UserDetails getUserDetailsFromRefreshToken(String refreshToken) {
        Claims claims = claimsProvider.getClaims(refreshToken);
        return getUserDetailsFromClaims(claims);
    }

}
