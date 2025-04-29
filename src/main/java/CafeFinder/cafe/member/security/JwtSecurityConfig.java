package CafeFinder.cafe.member.security;

import CafeFinder.cafe.global.infrastructure.redis.RefreshTokenService;
import CafeFinder.cafe.member.security.jwt.JwtAuthenticationProvider;
import CafeFinder.cafe.member.security.jwt.JwtFilter;
import CafeFinder.cafe.member.security.jwt.JwtValidator;
import CafeFinder.cafe.member.security.jwt.RefreshTokenFilter;
import CafeFinder.cafe.member.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfig
        extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtValidator validator;
    private final JwtAuthenticationProvider authenticationProvider;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void configure(HttpSecurity http) {
        JwtFilter jwtFilter = new JwtFilter(validator, authenticationProvider);
        RefreshTokenFilter refreshTokenFilter = new RefreshTokenFilter(
                validator, tokenProvider, refreshTokenService, authenticationProvider);

        http
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(refreshTokenFilter, JwtFilter.class);

    }

}
