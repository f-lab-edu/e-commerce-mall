package com.ecommerce.jwt.repository;

import com.ecommerce.jwt.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  RefreshToken findByEmailAndRefreshToken(String email, String refreshToken);

  Boolean existsByRefreshToken(String refreshToken);

  @Transactional
  void deleteByRefreshToken(String refreshToken);

  @Transactional
  void deleteByEmail(String email);
}
