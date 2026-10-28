package com.example.eshop.cart;

import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class CartItem implements java.io.Serializable {
  private Long productId;
  private String name;
  private String imageUrl;
  private BigDecimal price;
  private int quantity;

  public BigDecimal getSubtotal() {
    return price.multiply(BigDecimal.valueOf(quantity));
  }
}
