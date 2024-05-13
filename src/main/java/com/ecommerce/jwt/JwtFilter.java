package com.ecommerce.jwt;

import com.ecommerce.member.dto.MemberDetails;
import com.ecommerce.member.entity.Member;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private static final String ACCESS_TOKEN_KEY = "Access";
    private static final String ACCESS_TOKEN_CATEGORY = "access";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        logger.debug("JwtFilter start.");

        String accessToken = request.getHeader(ACCESS_TOKEN_KEY);

        if(accessToken == null) {
            logger.debug("token null");
            filterChain.doFilter(request, response);
            return;
        }

        logger.debug("authorization now");

        // expired check
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            logger.debug("access token expired");
            // response to client
            PrintWriter writer = response.getWriter();
            writer.println("access token expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // category check
        String category = jwtUtil.getCategory(accessToken);

        if(!category.equals(ACCESS_TOKEN_CATEGORY)) {
            logger.debug("invalid access token");
            PrintWriter writer = response.getWriter();
            writer.println("invalid access token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = jwtUtil.getEmail(accessToken);

        Member member = Member.builder()
                .email(email)
                .build();

        MemberDetails memberDetails = new MemberDetails(member);
        Authentication authToken = new UsernamePasswordAuthenticationToken(memberDetails, null, memberDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
