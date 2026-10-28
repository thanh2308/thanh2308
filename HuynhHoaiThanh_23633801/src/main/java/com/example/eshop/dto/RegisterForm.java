package com.example.eshop.dto;

import com.example.eshop.entity.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class RegisterForm {
  @NotBlank
  @Size(min = 4, max = 50)
  @Pattern(regexp = "[a-zA-Z0-9_]+")
  private String username;

  @NotBlank
  @Size(min = 6, max = 50)
  private String password;

  @NotBlank private String confirmPassword;

  @NotBlank
  @Size(max = 100)
  private String fullName;

  @NotBlank
  @Email
  @Size(max = 100)
  private String email;

  @Pattern(regexp = "^$|0[0-9]{9}", message = "SĐT gồm 10 số, bắt đầu bằng 0")
  private String phone = "";

  @Size(max = 255)
  private String address;
}
