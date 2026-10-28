package com.example.eshop.controller;

import com.example.eshop.dto.*;
import com.example.eshop.entity.*;
import com.example.eshop.security.JwtService;
import com.example.eshop.service.*;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {
  private final UserService users;
  private final AuthenticationManager manager;
  private final JwtService jwt;

  @Value("${app.jwt.cookie-secure:false}")
  private boolean secure;

  @GetMapping("/login")
  public String login() {
    return "auth/login";
  }

  @PostMapping("/login")
  public String authenticate(
      @RequestParam String username,
      @RequestParam String password,
      HttpServletRequest req,
      HttpServletResponse res) {
    try {
      var auth = manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
      // Rotate the session ID while retaining CSRF/cart data to prevent session fixation.
      if (req.getSession(false) != null) req.changeSessionId();
      String token = jwt.generateToken((UserDetails) auth.getPrincipal());
      res.addHeader(
          HttpHeaders.SET_COOKIE,
          ResponseCookie.from(JwtService.COOKIE_NAME, token)
              .httpOnly(true)
              .secure(secure)
              .sameSite("Lax")
              .path("/")
              .maxAge(jwt.getExpirationSeconds())
              .build()
              .toString());
      return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SELLER"))
          ? "redirect:/admin/products"
          : "redirect:/products";
    } catch (AuthenticationException e) {
      return "redirect:/login?error";
    }
  }

  @GetMapping("/register")
  public String form(Model m) {
    m.addAttribute("registerForm", new RegisterForm());
    return "auth/register";
  }

  @PostMapping("/register")
  public String register(
      @Valid @ModelAttribute("registerForm") RegisterForm f, BindingResult result) {
    if (result.hasErrors()) return "auth/register";
    try {
      users.register(f);
    } catch (IllegalArgumentException | org.springframework.dao.DataIntegrityViolationException e) {
      result.reject("register", "Tên đăng nhập/email đã tồn tại hoặc mật khẩu xác nhận không khớp");
      return "auth/register";
    }
    return "redirect:/login?registered";
  }
}
