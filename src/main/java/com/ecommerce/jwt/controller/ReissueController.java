package com.ecommerce.jwt.controller;

import com.ecommerce.jwt.dto.ReissueResponse;
import com.ecommerce.jwt.service.ReissueService;
import com.ecommerce.utils.CookieUtil;
import jakarta.servlet.http.Cookie;
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
        String refreshToken = null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("Refresh")) {
                refreshToken = cookie.getValue();
            }
        }

        if (refreshToken == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        ReissueResponse reissueResponse = reissueService.reissue(refreshToken);

        if(reissueResponse.getAccessToken() == null) {
            return new ResponseEntity<>(reissueResponse.getMessage(), HttpStatus.BAD_REQUEST);
        }

        response.setHeader("Access", reissueResponse.getAccessToken());
        response.addCookie(CookieUtil.createCookie("Refresh", reissueResponse.getRefreshToke()));

        return ResponseEntity.ok().build();
    }
}
