package com.example.eshop.controller;

import com.example.eshop.cart.Cart;
import com.example.eshop.dto.*;
import com.example.eshop.entity.*;
import com.example.eshop.entity.Order;
import com.example.eshop.service.*;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {
  private final Cart cart;
  private final OrderService orders;
  private final UserService users;

  @GetMapping
  public String form(Model m, Principal p) {
    if (cart.isEmpty()) return "redirect:/cart";
    CheckoutForm f = new CheckoutForm();
    User u = users.findByUsername(p.getName());
    f.setCustomerName(u.getFullName());
    f.setPhone(u.getPhone());
    f.setAddress(u.getAddress());
    m.addAttribute("checkoutForm", f);
    m.addAttribute("cart", cart);
    return "checkout/form";
  }

  @PostMapping
  public String submit(
      @Valid @ModelAttribute("checkoutForm") CheckoutForm f,
      BindingResult result,
      Model m,
      Principal p) {
    if (cart.isEmpty()) return "redirect:/cart";
    m.addAttribute("cart", cart);
    if (result.hasErrors()) return "checkout/form";
    try {
      Order o = orders.placeOrder(f, cart.getItems(), p.getName());
      cart.clear();
      return "redirect:/orders/"
          + o.getId()
          + (o.getPaymentMethod() == PaymentMethod.BANKING ? "/payment" : "");
    } catch (IllegalArgumentException e) {
      result.reject("checkout", e.getMessage());
      return "checkout/form";
    }
  }
}
