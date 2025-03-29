package CafeFinder.cafe.config;

import CafeFinder.cafe.infrastructure.auth.CustomOAuth2MemberService;
import CafeFinder.cafe.infrastructure.auth.OAuth2AuthenticationSuccessHandler;
import CafeFinder.cafe.infrastructure.auth.email.CompositeEmailExtractor;
import CafeFinder.cafe.infrastructure.jwt.JwtAccessDeniedHandler;
import CafeFinder.cafe.infrastructure.jwt.JwtAuthenticationEntryPoint;
import CafeFinder.cafe.infrastructure.jwt.TokenProvider;
import CafeFinder.cafe.service.interfaces.MemberService;
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
    private final @Lazy CustomOAuth2MemberService customOAuth2UserService;

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
                                                                                 CompositeEmailExtractor compositeEmailExtractor
    ) {
        return new OAuth2AuthenticationSuccessHandler(memberService, tokenProvider,
                compositeEmailExtractor);
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

}
