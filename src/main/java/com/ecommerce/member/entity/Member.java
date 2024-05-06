package com.ecommerce.member.entity;

import com.ecommerce.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  @NotNull
  private String email;

  @NotNull private String password;
  @NotNull private String name;
  @NotNull private String phone;
  private String keywords;

  public boolean checkPassword(String password) {
    return this.password.equals(password);
  }
}
