package com.ecommerce.jwt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String email;
    @NotNull
    private String refreshToken;
    @NotNull
    private String expiration;

    public RefreshToken update(String refreshToken, Long expiredMs) {
        this.refreshToken = refreshToken;
        setExpiration(expiredMs);
        return this;
    }

    public void setExpiration(Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);
        this.expiration = date.toString();
    }
}
