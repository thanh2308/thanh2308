package com.example.eshop.controller;

import com.example.eshop.cart.Cart;
import com.example.eshop.dto.*;
import com.example.eshop.entity.*;
import com.example.eshop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {
  private final Cart cart;

  @ModelAttribute("cartCount")
  public int cartCount(jakarta.servlet.http.HttpServletRequest r) {
    return r.getRequestURI().startsWith("/api/") ? 0 : cart.getTotalQuantity();
  }
}
