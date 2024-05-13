package com.ecommerce.jwt;

import com.ecommerce.jwt.entity.RefreshToken;
import com.ecommerce.jwt.repository.RefreshTokenRepository;
import com.ecommerce.member.dto.SigninRequest;
import com.ecommerce.utils.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class SigninFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private static final Long ACCESS_TOKEN_EXPIREDMS = 600000L;
    private static final Long REFRESH_TOKEN_EXPIREDMS = 86400000L;
    private static final String ACCESS_TOKEN_KEY = "Access";
    private static final String REFRESH_TOKEN_KEY = "Refresh";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        SigninRequest signinRequest;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            signinRequest = objectMapper.readValue(messageBody, SigninRequest.class);
        } catch (IOException e) {
            throw new RuntimeException();
        }

        String email = signinRequest.getEmail();
        String password = signinRequest.getPassword();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String email = authResult.getName();

        String accessToken = jwtUtil.createJWT("access", email, ACCESS_TOKEN_EXPIREDMS);
        String refreshToken = jwtUtil.createJWT("refresh", email, REFRESH_TOKEN_EXPIREDMS);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .email(email)
                .refreshToken(refreshToken)
                .build();
        refreshTokenEntity.setExpiration(REFRESH_TOKEN_EXPIREDMS);
        refreshTokenRepository.save(refreshTokenEntity);

        response.setHeader(ACCESS_TOKEN_KEY, accessToken);
        response.addCookie(CookieUtil.createCookie(REFRESH_TOKEN_KEY, refreshToken));
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(401);
    }
}
