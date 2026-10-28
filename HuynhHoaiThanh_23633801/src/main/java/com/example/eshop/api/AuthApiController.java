package com.example.eshop.api;

import com.example.eshop.security.JwtService;
import com.example.eshop.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {
  private final AuthenticationManager manager;
  private final JwtService jwt;

  public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

  public record TokenResponse(String accessToken, String tokenType, long expiresIn) {}

  @PostMapping("/login")
  public TokenResponse login(@Valid @RequestBody LoginRequest r) {
    var a =
        manager.authenticate(new UsernamePasswordAuthenticationToken(r.username(), r.password()));
    return new TokenResponse(
        jwt.generateToken((UserDetails) a.getPrincipal()), "Bearer", jwt.getExpirationSeconds());
  }
}
