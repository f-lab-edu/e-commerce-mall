package com.ecommerce.jwt;

import com.ecommerce.member.dto.MemberDetails;
import com.ecommerce.member.entity.Member;
import com.ecommerce.utils.HeaderUtil;
import com.ecommerce.member.entity.Role;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    logger.debug("JwtFilter start.");

    String accessToken = HeaderUtil.getAccessToken(request);

    if (accessToken == null) {
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
    if (!jwtUtil.isAccessToken(accessToken)) {
      logger.debug("invalid access token");
      PrintWriter writer = response.getWriter();
      writer.println("invalid access token");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    String email = jwtUtil.getEmail(accessToken);
    Role role = jwtUtil.getRole(accessToken);
    log.debug("role from token :: " + role);

    Member member = Member.builder().email(email).role(role).build();
    log.debug("role Role :: " + member.getRole());

    MemberDetails memberDetails = new MemberDetails(member);
    Authentication authToken = new UsernamePasswordAuthenticationToken(memberDetails, null,
        memberDetails.getAuthorities());
    log.debug("authToken :: " + authToken);
    SecurityContextHolder.getContext().setAuthentication(authToken);
    filterChain.doFilter(request, response);
  }
}
