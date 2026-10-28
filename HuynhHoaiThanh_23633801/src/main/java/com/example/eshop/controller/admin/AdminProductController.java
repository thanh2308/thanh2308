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
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
  private final ProductService service;
  private final CategoryService categories;

  @GetMapping
  public String list(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(defaultValue = "0") int page,
      Model m) {
    m.addAttribute("page", service.search(keyword, categoryId, page, 10));
    m.addAttribute("keyword", keyword);
    m.addAttribute("categoryId", categoryId);
    m.addAttribute("categories", categories.findAll());
    return "admin/product/list";
  }

  @GetMapping("/new")
  public String create(Model m) {
    m.addAttribute("product", new Product());
    m.addAttribute("categories", categories.findAll());
    return "admin/product/form";
  }

  @GetMapping("/{id}/edit")
  public String edit(@PathVariable Long id, Model m) {
    m.addAttribute("product", service.findById(id));
    m.addAttribute("categories", categories.findAll());
    return "admin/product/form";
  }

  @PostMapping("/save")
  public String save(
      @Valid @ModelAttribute("product") Product f,
      BindingResult result,
      @RequestParam Long categoryId,
      Model m,
      RedirectAttributes ra) {
    if (result.hasErrors()) {
      m.addAttribute("categories", categories.findAll());
      return "admin/product/form";
    }
    try {
      service.save(f, categoryId);
    } catch (org.springframework.dao.DataIntegrityViolationException e) {
      result.reject("save", "Dữ liệu bị trùng hoặc có ràng buộc liên quan");
      m.addAttribute("categories", categories.findAll());
      return "admin/product/form";
    }
    ra.addFlashAttribute("message", "Đã lưu thành công");
    return "redirect:/admin/products";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, RedirectAttributes ra) {
    try {
      service.delete(id);
      ra.addFlashAttribute("message", "Đã xóa");
    } catch (IllegalStateException | org.springframework.dao.DataIntegrityViolationException e) {
      ra.addFlashAttribute("error", "Không thể xóa: dữ liệu đang được sử dụng");
    }
    return "redirect:/admin/products";
  }
}
