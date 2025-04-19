package CafeFinder.cafe.member.security.jwt;

import CafeFinder.cafe.member.dto.TokenDto;
import CafeFinder.cafe.member.security.config.Jwtconfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {


    private final Jwtconfig jwtconfig;


    private final SecretKey jwtSecretKey;


    public TokenDto generateTokenDto(Authentication authentication) {

        String authority = extractAuthories(authentication);

        String accessToken = createAccessToken(authentication.getName(), authority);
        String refreshToken = createRefreshToken(authentication.getName());

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(jwtconfig.getValidation().getAccess().intValue())
                .refreshTokenExpiresIn(jwtconfig.getValidation().getRefresh().intValue())
                .build();
    }

    private String extractAuthories(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private String createAccessToken(String email, String authorities) {
        return Jwts.builder()
                .claim("email", email)
                .claim("role", authorities)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtconfig.getValidation().getAccess()))
                .signWith(jwtSecretKey, SIG.HS256)
                .compact();
    }

    private String createRefreshToken(String email) {
        return Jwts.builder()
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtconfig.getValidation().getRefresh()))
                .signWith(jwtSecretKey, SIG.HS256)
                .compact();
    }

}
