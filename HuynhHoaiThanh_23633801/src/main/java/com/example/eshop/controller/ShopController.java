package com.example.eshop.controller;

import com.example.eshop.dto.*;
import com.example.eshop.entity.*;
import com.example.eshop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ShopController {
  private final ProductService products;
  private final CategoryService categories;

  @GetMapping({"/", "/products"})
  public String index(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(defaultValue = "0") int page,
      Model m) {
    m.addAttribute("page", products.search(keyword, categoryId, page, 12));
    m.addAttribute("categories", categories.findAll());
    m.addAttribute("keyword", keyword);
    m.addAttribute("categoryId", categoryId);
    return "shop/index";
  }

  @GetMapping("/products/{id}")
  public String detail(@PathVariable Long id, Model m) {
    m.addAttribute("product", products.findById(id));
    return "shop/detail";
  }
}
