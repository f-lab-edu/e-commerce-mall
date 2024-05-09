package com.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((auth) -> auth
                        // 검색, 조회는 비회원, 회원 모두 허용
                        .requestMatchers("/", "/categories/**", "/products/**", "/search").permitAll()
                        // 그 외 모든 기능은 회원만 허용
                        .anyRequest().authenticated());
        return http.build();
    }
}
