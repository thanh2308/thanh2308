package com.example.eshop.service;

import com.example.eshop.entity.*;
import com.example.eshop.exception.NotFoundException;
import com.example.eshop.repository.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductRepository products;
  private final CategoryService categories;

  public Page<Product> search(String keyword, Long categoryId, int page, int size) {
    return products.search(
        keyword == null || keyword.isBlank() ? null : keyword.trim(),
        categoryId,
        PageRequest.of(
            Math.max(0, page), Math.max(1, Math.min(size, 100)), Sort.by("id").descending()));
  }

  public Product findById(Long id) {
    return products
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));
  }

  @PreAuthorize("hasRole('SELLER')")
  @Transactional
  public Product save(Product form, Long categoryId) {
    Product p = form.getId() == null ? new Product() : findById(form.getId());
    p.setName(form.getName().trim());
    p.setDescription(form.getDescription());
    p.setPrice(form.getPrice());
    p.setQuantity(form.getQuantity());
    p.setImageUrl(form.getImageUrl());
    p.setCategory(categories.findById(categoryId));
    return products.saveAndFlush(p);
  }

  @PreAuthorize("hasRole('SELLER')")
  @Transactional
  public void delete(Long id) {
    findById(id);
    if (products.usedInOrder(id))
      throw new IllegalStateException("Sản phẩm đã có trong đơn hàng, không thể xóa");
    products.deleteById(id);
    products.flush();
  }
}
