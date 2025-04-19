package CafeFinder.cafe.member.security;

import CafeFinder.cafe.member.security.jwt.JwtAuthenticationProvider;
import CafeFinder.cafe.member.security.jwt.JwtFilter;
import CafeFinder.cafe.member.security.jwt.JwtValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtValidator validator;
    private final JwtAuthenticationProvider authenticationProvider;

    @Override
    public void configure(HttpSecurity httpSecurity) {
        JwtFilter jwtFilter = new JwtFilter(validator, authenticationProvider);
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

}


