package com.example.eshop.controller.admin;

import com.example.eshop.dto.*;
import com.example.eshop.entity.*;
import com.example.eshop.service.*;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
  private final UserService users;

  private String form(Model m, UserForm f) {
    m.addAttribute("userForm", f);
    m.addAttribute("roles", Role.values());
    return "admin/user/form";
  }

  @GetMapping
  public String list(Model m) {
    m.addAttribute("users", users.findAll());
    return "admin/user/list";
  }

  @GetMapping("/new")
  public String create(Model m) {
    return form(m, new UserForm());
  }

  @GetMapping("/{id}/edit")
  public String edit(@PathVariable Long id, Model m) {
    return form(m, UserForm.from(users.findById(id)));
  }

  @PostMapping("/save")
  public String save(
      @Valid @ModelAttribute("userForm") UserForm f,
      BindingResult result,
      Model m,
      Principal p,
      RedirectAttributes ra) {
    if (result.hasErrors()) return form(m, f);
    try {
      users.saveByAdmin(f, p.getName());
    } catch (IllegalArgumentException | org.springframework.dao.DataIntegrityViolationException e) {
      result.reject(
          "save",
          "Không thể lưu: kiểm tra tài khoản/email trùng, mật khẩu và quyền tài khoản đang dùng");
      return form(m, f);
    }
    ra.addFlashAttribute("message", "Đã lưu tài khoản");
    return "redirect:/admin/users";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, Principal p, RedirectAttributes ra) {
    try {
      users.delete(id, p.getName());
      ra.addFlashAttribute("message", "Đã xóa tài khoản");
    } catch (IllegalStateException e) {
      ra.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/admin/users";
  }
}
