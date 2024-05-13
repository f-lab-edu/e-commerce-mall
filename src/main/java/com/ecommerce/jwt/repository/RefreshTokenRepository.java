package com.ecommerce.jwt.repository;

import com.ecommerce.jwt.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByEmailAndRefreshToken(String email, String refreshToken);
}
