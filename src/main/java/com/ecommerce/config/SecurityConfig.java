package com.ecommerce.config;

import com.ecommerce.jwt.JwtFilter;
import com.ecommerce.jwt.JwtUtil;
import com.ecommerce.jwt.SigninFilter;
import com.ecommerce.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.security.debug:false}")
    boolean securityDebug;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(securityDebug);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests((auth) -> auth
                // 검색, 조회, 로그인, 회원가입은 비회원, 회원 모두 허용
                .requestMatchers("/", "/categories/**", "/products/**", "/search", "/signin", "/reissue").permitAll()
                .requestMatchers(HttpMethod.POST, "/members").permitAll()
                // 그 외 모든 기능은 회원만 허용
                .anyRequest().authenticated()
        );

        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 로그인 필터 등록
        SigninFilter signinFilter = new SigninFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshTokenRepository);
        signinFilter.setFilterProcessesUrl("/signin");
        signinFilter.setUsernameParameter("email");

        http.addFilterAt(signinFilter, UsernamePasswordAuthenticationFilter.class);

        // JwtFilter 등록
        http.addFilterBefore(new JwtFilter(jwtUtil), SigninFilter.class);




        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
