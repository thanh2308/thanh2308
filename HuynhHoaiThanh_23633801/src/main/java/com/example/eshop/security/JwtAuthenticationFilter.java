package com.example.eshop.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwt;
  private final UserDetailsService users;
  private final boolean api;

  public JwtAuthenticationFilter(JwtService j, UserDetailsService u, boolean api) {
    jwt = j;
    users = u;
    this.api = api;
  }

  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String token = null;
    // API never accepts cookies: CSRF is disabled only for header-based authentication.
    if (api) {
      String h = req.getHeader("Authorization");
      if (h != null && h.startsWith("Bearer ")) token = h.substring(7);
    } else if (req.getCookies() != null)
      for (Cookie c : req.getCookies())
        if (JwtService.COOKIE_NAME.equals(c.getName())) token = c.getValue();
    if (token != null && SecurityContextHolder.getContext().getAuthentication() == null)
      try {
        UserDetails u = users.loadUserByUsername(jwt.extractUsername(token));
        if (u.isEnabled()
            && u.isAccountNonLocked()
            && u.isAccountNonExpired()
            && u.isCredentialsNonExpired()) {
          var context = SecurityContextHolder.createEmptyContext();
          context.setAuthentication(
              new UsernamePasswordAuthenticationToken(u, null, u.getAuthorities()));
          SecurityContextHolder.setContext(context);
        }
      } catch (JwtException | IllegalArgumentException | UsernameNotFoundException ignored) {
      }
    chain.doFilter(req, res);
  }
}
