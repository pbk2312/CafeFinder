package CafeFinder.cafe.global.config;

import CafeFinder.cafe.member.config.JwtSecurityConfig;
import CafeFinder.cafe.member.oAuth.OAuth2AuthenticationSuccessHandler;
import CafeFinder.cafe.member.oAuth.OAuth2MemberService;
import CafeFinder.cafe.member.jwt.JwtAccessDeniedHandler;
import CafeFinder.cafe.member.jwt.JwtAuthenticationEntryPoint;
import CafeFinder.cafe.member.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2MemberService oAuth2MemberService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .formLogin(form -> form.loginPage("/member/signupAndLogin"))
                .authorizeHttpRequests(auth -> {
                            auth.requestMatchers("/member/signupAndLogin").anonymous()
                                    .requestMatchers("/member/profile", "/member/edit").authenticated()
                                    .anyRequest().permitAll();
                        }
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/member/signupAndLogin")
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .userInfoEndpoint(userInfoEndpoint ->
                                userInfoEndpoint.userService(oAuth2MemberService)
                        )
                )
                .with(new JwtSecurityConfig(tokenProvider), jwtSecurityConfig -> {
                });

        return http.build();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

}
