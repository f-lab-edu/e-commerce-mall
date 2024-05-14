package com.ecommerce.jwt.entity;

import com.ecommerce.jwt.JwtUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
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

    public void update(String refreshToken) {
        this.refreshToken = refreshToken;
        setExpiration();
    }

    public void setExpiration() {
        Date date = new Date(System.currentTimeMillis() + JwtUtil.REFRESH_TOKEN_EXPIREDMS);
        this.expiration = date.toString();
    }
}
