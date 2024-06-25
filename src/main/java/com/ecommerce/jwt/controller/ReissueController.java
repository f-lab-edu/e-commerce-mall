package com.ecommerce.jwt.controller;

import com.ecommerce.exception.ExpiredTokenException;
import com.ecommerce.exception.InvalidTokenException;
import com.ecommerce.jwt.dto.ReissueResponse;
import com.ecommerce.jwt.dto.TokenPair;
import com.ecommerce.jwt.service.ReissueService;
import com.ecommerce.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reissue")
public class ReissueController {

  private final ReissueService reissueService;

  @PostMapping("")
  public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = CookieUtil.getRefreshToken(request.getCookies());

    if (refreshToken == null) {
      return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
    }

    try {
      TokenPair tokens = reissueService.reissue(refreshToken);
      response.setHeader("Access", tokens.getAccessToken());
      response.addCookie(CookieUtil.createRefreshCookie(tokens.getRefreshToken()));
      return ResponseEntity.ok().body(ReissueResponse.builder()
          .accessToken(tokens.getAccessToken())
          .refreshToken(tokens.getRefreshToken())
          .build());
    } catch (ExpiredTokenException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ReissueResponse.builder().message(e.getMessage()));
    } catch (InvalidTokenException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ReissueResponse.builder().message(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ReissueResponse.builder().message(e.getMessage()));
    }
  }
}
