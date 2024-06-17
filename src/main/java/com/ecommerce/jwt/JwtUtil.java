package com.ecommerce.jwt;

import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private static final String JWT_PAYLOAD_CATEGORY = "category";
  private static final String JWT_PAYLOAD_EMAIL = "email";
  private static final String ACCESS_TOKEN_CATEGORY = "access";
  private static final String REFRESH_TOKEN_CATEGORY = "refresh";
  private static final Long ACCESS_TOKEN_EXPIRATION_MS = 600000L;
  public static final Long REFRESH_TOKEN_EXPIRATION_MS = 86400000L;
  private SecretKey secretKey;

  public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
    this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm());
  }

  public Boolean isAccessToken(String token) {
    return ACCESS_TOKEN_CATEGORY.equals(getCategory(token));
  }

  public Boolean isRefreshToken(String token) {
    return REFRESH_TOKEN_CATEGORY.equals(getCategory(token));
  }

  public String getCategory(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .get(JWT_PAYLOAD_CATEGORY, String.class);
  }

  public String getEmail(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .get(JWT_PAYLOAD_EMAIL, String.class);
  }

  public Boolean isExpired(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .getExpiration().before(new Date());
  }

  public String createAccessToken(String email) {
    return createJWT(ACCESS_TOKEN_CATEGORY, email, ACCESS_TOKEN_EXPIRATION_MS);
  }

  public String createRefreshToken(String email) {
    return createJWT(REFRESH_TOKEN_CATEGORY, email, REFRESH_TOKEN_EXPIRATION_MS);
  }

  private String createJWT(String category, String email, Long expiredMs) {
    return Jwts.builder().claim(JWT_PAYLOAD_CATEGORY, category).claim(JWT_PAYLOAD_EMAIL, email)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiredMs)).signWith(secretKey).compact();
  }
}
