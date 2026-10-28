package com.example.eshop.api;

import com.example.eshop.exception.NotFoundException;
import com.example.eshop.service.*;
import jakarta.validation.constraints.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice(basePackages = "com.example.eshop.api")
public class ApiExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, String> missing(NotFoundException e) {
    return Map.of("error", e.getMessage());
  }

  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public Map<String, String> login() {
    return Map.of("error", "Sai tài khoản, mật khẩu hoặc tài khoản bị khóa");
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Map<String, String> forbidden() {
    return Map.of("error", "Không có quyền thực hiện");
  }

  @ExceptionHandler({
    IllegalArgumentException.class,
    org.springframework.web.bind.MethodArgumentNotValidException.class,
    org.springframework.http.converter.HttpMessageNotReadableException.class,
    org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> invalid() {
    return Map.of("error", "Dữ liệu không hợp lệ");
  }

  @ExceptionHandler({
    IllegalStateException.class,
    org.springframework.dao.DataIntegrityViolationException.class
  })
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, String> conflict() {
    return Map.of("error", "Dữ liệu bị trùng hoặc đang được sử dụng");
  }
}
