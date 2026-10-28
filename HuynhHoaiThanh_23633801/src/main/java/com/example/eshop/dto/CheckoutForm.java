package com.example.eshop.dto;

import com.example.eshop.entity.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class CheckoutForm {
  @NotBlank
  @Size(max = 100)
  private String customerName;

  @NotBlank
  @Pattern(regexp = "0[0-9]{9}", message = "SĐT gồm 10 số, bắt đầu bằng 0")
  private String phone;

  @NotBlank
  @Size(max = 255)
  private String address;

  @Size(max = 500)
  private String note;

  @NotNull private PaymentMethod paymentMethod = PaymentMethod.COD;
}
