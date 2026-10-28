package com.example.eshop.dto;

import com.example.eshop.entity.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class UserForm {
  private Long id;

  @NotBlank
  @Size(min = 4, max = 50)
  @Pattern(regexp = "[a-zA-Z0-9_]+")
  private String username;

  @Size(max = 50)
  private String password = "";

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

  @NotNull private Role role = Role.ROLE_CUSTOMER;
  private boolean enabled = true;

  public static UserForm from(User u) {
    UserForm f = new UserForm();
    f.id = u.getId();
    f.username = u.getUsername();
    f.fullName = u.getFullName();
    f.email = u.getEmail();
    f.phone = u.getPhone();
    f.address = u.getAddress();
    f.role = u.getRole();
    f.enabled = u.isEnabled();
    return f;
  }
}
