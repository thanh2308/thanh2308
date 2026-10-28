package com.example.eshop.controller;

import com.example.eshop.cart.Cart;
import com.example.eshop.dto.*;
import com.example.eshop.entity.*;
import com.example.eshop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
  private final Cart cart;
  private final ProductService products;

  @GetMapping
  public String view(Model m) {
    m.addAttribute("cart", cart);
    return "cart/view";
  }

  @PostMapping("/add")
  public String add(
      @RequestParam Long productId,
      @RequestParam(defaultValue = "1") int quantity,
      RedirectAttributes ra) {
    try {
      cart.add(products.findById(productId), quantity);
      ra.addFlashAttribute("message", "Đã thêm vào giỏ");
    } catch (IllegalArgumentException e) {
      ra.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/cart";
  }

  @PostMapping("/update")
  public String update(
      @RequestParam Long productId, @RequestParam int quantity, RedirectAttributes ra) {
    try {
      cart.update(products.findById(productId), quantity);
    } catch (IllegalArgumentException e) {
      ra.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/cart";
  }

  @PostMapping("/remove/{id}")
  public String remove(@PathVariable Long id) {
    cart.remove(id);
    return "redirect:/cart";
  }

  @PostMapping("/clear")
  public String clear() {
    cart.clear();
    return "redirect:/cart";
  }
}
