package com.example.eshop.controller;

import com.example.eshop.dto.*;
import com.example.eshop.entity.*;
import com.example.eshop.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ErrorPageController {
  @RequestMapping("/403")
  public String forbidden(jakarta.servlet.http.HttpServletResponse response) {
    response.setStatus(403);
    return "error/403";
  }
}
