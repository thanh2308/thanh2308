package com.example.eshop.controller.admin;

import com.example.eshop.dto.*;
import com.example.eshop.entity.*;
import com.example.eshop.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
  private final CategoryService service;

  @GetMapping
  public String list(Model m) {
    m.addAttribute("categories", service.findAll());
    return "admin/category/list";
  }

  @GetMapping("/new")
  public String create(Model m) {
    m.addAttribute("category", new Category());
    return "admin/category/form";
  }

  @GetMapping("/{id}/edit")
  public String edit(@PathVariable Long id, Model m) {
    m.addAttribute("category", service.findById(id));
    return "admin/category/form";
  }

  @PostMapping("/save")
  public String save(
      @Valid @ModelAttribute("category") Category f,
      BindingResult result,
      Model m,
      RedirectAttributes ra) {
    if (result.hasErrors()) {
      return "admin/category/form";
    }
    try {
      service.save(f);
    } catch (org.springframework.dao.DataIntegrityViolationException e) {
      result.reject("save", "Dữ liệu bị trùng hoặc có ràng buộc liên quan");
      return "admin/category/form";
    }
    ra.addFlashAttribute("message", "Đã lưu thành công");
    return "redirect:/admin/categories";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, RedirectAttributes ra) {
    try {
      service.delete(id);
      ra.addFlashAttribute("message", "Đã xóa");
    } catch (IllegalStateException | org.springframework.dao.DataIntegrityViolationException e) {
      ra.addFlashAttribute("error", "Không thể xóa: dữ liệu đang được sử dụng");
    }
    return "redirect:/admin/categories";
  }
}
