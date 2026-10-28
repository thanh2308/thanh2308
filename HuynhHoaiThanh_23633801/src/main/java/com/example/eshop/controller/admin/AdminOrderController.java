package com.example.eshop.controller.admin;

import com.example.eshop.dto.*;
import com.example.eshop.entity.*;
import com.example.eshop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
  private final OrderService orders;

  @GetMapping
  public String list(Model m) {
    m.addAttribute("orders", orders.findAll());
    return "admin/order/list";
  }

  @GetMapping("/{id}")
  public String detail(@PathVariable Long id, Model m) {
    m.addAttribute("order", orders.findById(id));
    m.addAttribute("statuses", OrderStatus.values());
    return "admin/order/detail";
  }

  @PostMapping("/{id}/status")
  public String status(
      @PathVariable Long id, @RequestParam OrderStatus status, RedirectAttributes ra) {
    try {
      orders.updateStatus(id, status);
      ra.addFlashAttribute("message", "Đã cập nhật trạng thái");
    } catch (IllegalStateException e) {
      ra.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/admin/orders/" + id;
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, RedirectAttributes ra) {
    try {
      orders.delete(id);
      ra.addFlashAttribute("message", "Đã xóa đơn hàng");
    } catch (IllegalStateException e) {
      ra.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/admin/orders";
  }
}
