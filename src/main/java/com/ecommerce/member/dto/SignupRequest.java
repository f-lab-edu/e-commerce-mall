package com.ecommerce.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

  @NotBlank(message = "이메일은 필수입니다.")
  @Email(message = "올바른 이메일 형식을 입력하세요")
  private String email;

  @NotBlank(message = "비밀번호는 필수입니다.")
  @Pattern(regexp = "^(?![0-9]{10,}$)(?!.*(.)\\1{2,}).{10,}$", message = "비밀번호는 10자 이상 입력해야 하며, 숫자로만 이루어 질 수 없으며 3회 이상 연속되는 문자를 사용할 수 없습니다.")
  private String password;

  @NotBlank(message = "이름은 필수입니다.")
  private String name;

  @NotNull
  private String phone;
}
