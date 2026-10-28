package com.example.eshop.api;

import com.example.eshop.entity.Product;
import com.example.eshop.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApiController {
  private final ProductService products;

  public record ProductDto(
      Long id,
      String name,
      String description,
      BigDecimal price,
      Integer quantity,
      String imageUrl,
      Long categoryId,
      String categoryName) {
    static ProductDto from(Product p) {
      return new ProductDto(
          p.getId(),
          p.getName(),
          p.getDescription(),
          p.getPrice(),
          p.getQuantity(),
          p.getImageUrl(),
          p.getCategory().getId(),
          p.getCategory().getName());
    }
  }

  public record ProductRequest(
      @NotBlank @Size(max = 150) String name,
      @Size(max = 500) String description,
      @NotNull @DecimalMin("0") @Digits(integer = 13, fraction = 2) BigDecimal price,
      @NotNull @Min(0) Integer quantity,
      @Size(max = 500) String imageUrl,
      @NotNull Long categoryId) {}

  @GetMapping
  public Page<ProductDto> list(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return products.search(keyword, categoryId, page, size).map(ProductDto::from);
  }

  @GetMapping("/{id}")
  public ProductDto get(@PathVariable Long id) {
    return ProductDto.from(products.findById(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProductDto create(@Valid @RequestBody ProductRequest r) {
    return save(r, new Product());
  }

  @PutMapping("/{id}")
  public ProductDto update(@PathVariable Long id, @Valid @RequestBody ProductRequest r) {
    return save(r, products.findById(id));
  }

  private ProductDto save(ProductRequest r, Product p) {
    p.setName(r.name());
    p.setDescription(r.description());
    p.setPrice(r.price());
    p.setQuantity(r.quantity());
    p.setImageUrl(r.imageUrl());
    return ProductDto.from(products.save(p, r.categoryId()));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    products.delete(id);
  }
}
