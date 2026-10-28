package com.example.eshop.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.*;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final JwtService jwt;
  private final CustomUserDetailsService users;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration c)
      throws Exception {
    return c.getAuthenticationManager();
  }

  @Bean
  @Order(1)
  public SecurityFilterChain apiFilterChain(HttpSecurity h) throws Exception {
    return h.securityMatcher("/api/**")
        .csrf(AbstractHttpConfigurer::disable)
        .requestCache(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .anonymous(a -> a.authorities("ROLE_GUEST"))
        .authorizeHttpRequests(
            a ->
                a.requestMatchers("/api/auth/login")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**")
                    .permitAll()
                    .anyRequest()
                    .hasRole("SELLER"))
        .exceptionHandling(
            e ->
                e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                    .accessDeniedHandler((req, res, ex) -> res.sendError(403)))
        .addFilterBefore(
            new JwtAuthenticationFilter(jwt, users, true),
            UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain webFilterChain(HttpSecurity h) throws Exception {
    return h.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .anonymous(a -> a.authorities("ROLE_GUEST"))
        .authorizeHttpRequests(
            a ->
                a.dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ERROR)
                    .permitAll()
                    .requestMatchers(
                        "/",
                        "/products",
                        "/products/**",
                        "/login",
                        "/css/**",
                        "/images/**",
                        "/error",
                        "/403",
                        "/favicon.ico")
                    .permitAll()
                    .requestMatchers("/register")
                    .hasRole("GUEST")
                    .requestMatchers("/admin/**")
                    .hasRole("SELLER")
                    .requestMatchers(
                        "/cart", "/cart/**", "/checkout", "/checkout/**", "/orders", "/orders/**")
                    .hasRole("CUSTOMER")
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            e ->
                e.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                    .accessDeniedPage("/403"))
        .logout(
            l ->
                l.logoutUrl("/logout")
                    .deleteCookies(JwtService.COOKIE_NAME, "JSESSIONID")
                    .invalidateHttpSession(true)
                    .logoutSuccessUrl("/?logout"))
        .addFilterBefore(
            new JwtAuthenticationFilter(jwt, users, false),
            UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
