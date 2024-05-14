package com.ecommerce.jwt.service;

import com.ecommerce.jwt.JwtUtil;
import com.ecommerce.jwt.dto.ReissueResponse;
import com.ecommerce.jwt.entity.RefreshToken;
import com.ecommerce.jwt.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReissueService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public ReissueResponse reissue(String refreshToken) {
        // expired check
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            return ReissueResponse.builder()
                    .message("refresh token expired")
                    .build();
        }

        // category check
        if (!jwtUtil.isRefresh(refreshToken)) {
            return ReissueResponse.builder()
                    .message("invalid refresh token")
                    .build();
        }

        String email = jwtUtil.getEmail(refreshToken);

        // refresh in DB check
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByEmailAndRefreshToken(email, refreshToken);
        if (refreshTokenEntity == null) {
            return ReissueResponse.builder()
                    .message("invalid refresh token")
                    .build();
        }

        // make new JWT
        String newAccess = jwtUtil.createAccessToken(email);
        String newRefresh = jwtUtil.createRefreshToken(email);

        refreshTokenEntity.update(newRefresh);

        return ReissueResponse.builder()
                .accessToken(newAccess)
                .refreshToke(newRefresh)
                .message("success")
                .build();
    }
}
