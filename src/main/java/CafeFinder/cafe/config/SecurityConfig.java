package CafeFinder.cafe.config;

import CafeFinder.cafe.auth.CustomOAuth2UserService;
import CafeFinder.cafe.auth.OAuth2AuthenticationSuccessHandler;
import CafeFinder.cafe.auth.email.CompositeEmailExtractor;
import CafeFinder.cafe.jwt.JwtAccessDeniedHandler;
import CafeFinder.cafe.jwt.JwtAuthenticationEntryPoint;
import CafeFinder.cafe.jwt.TokenProvider;
import CafeFinder.cafe.service.interfaces.MemberService;
import CafeFinder.cafe.service.redis.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final @Lazy CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2AuthenticationSuccessHandler successHandler)
            throws Exception {
        http
                .csrf(CsrfConfigurer::disable)

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/member/signupAndLogin",
                                "/api/member/login",
                                "/api/member/signUp",
                                "/api/email/sendCode",
                                "/api/email/verifyCode",
                                "/"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/member/signupAndLogin")
                        .successHandler(successHandler)
                        // 로그인 성공 시 사용자 정보 처리
                        .userInfoEndpoint(userInfoEndpoint ->
                                userInfoEndpoint.userService(customOAuth2UserService)
                        )
                )
                .with(new JwtSecurityConfig(tokenProvider), jwtSecurityConfig -> {
                });

        return http.build();
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler(MemberService memberService,
                                                                                 TokenProvider tokenProvider,
                                                                                 RefreshTokenService refreshTokenService,
                                                                                 CompositeEmailExtractor compositeEmailExtractor
    ) {
        return new OAuth2AuthenticationSuccessHandler(memberService, tokenProvider, refreshTokenService,
                compositeEmailExtractor);
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

}
