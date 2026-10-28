package com.example.eshop.controller;

import com.example.eshop.dto.*;
import com.example.eshop.entity.*;
import com.example.eshop.service.*;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
  private final OrderService orders;

  @GetMapping
  public String mine(Principal p, Model m) {
    m.addAttribute("orders", orders.myOrders(p.getName()));
    return "order/my-orders";
  }

  @GetMapping("/{id}")
  public String detail(@PathVariable Long id, Principal p, Model m) {
    m.addAttribute("order", orders.getOwnedOrder(id, p.getName()));
    return "order/detail";
  }

  @GetMapping("/{id}/payment")
  public String payment(@PathVariable Long id, Principal p, Model m) {
    m.addAttribute("order", orders.getOwnedOrder(id, p.getName()));
    return "order/payment";
  }

  @PostMapping({"/{id}/pay", "/{id}/confirm-payment"})
  public String confirm(@PathVariable Long id, Principal p, RedirectAttributes ra) {
    try {
      orders.confirmPayment(id, p.getName());
      ra.addFlashAttribute("message", "Đã ghi nhận thanh toán giả lập");
    } catch (IllegalStateException e) {
      ra.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/orders/" + id;
  }
}
