package com.example.eshop.service;

import com.example.eshop.entity.*;
import com.example.eshop.exception.NotFoundException;
import com.example.eshop.repository.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepository categories;
  private final ProductRepository products;

  public List<Category> findAll() {
    return categories.findAllByOrderByNameAsc();
  }

  public Category findById(Long id) {
    return categories
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục"));
  }

  @PreAuthorize("hasRole('SELLER')")
  @Transactional
  public Category save(Category form) {
    Category c = form.getId() == null ? new Category() : findById(form.getId());
    c.setName(form.getName().trim());
    c.setDescription(form.getDescription());
    return categories.saveAndFlush(c);
  }

  @PreAuthorize("hasRole('SELLER')")
  @Transactional
  public void delete(Long id) {
    findById(id);
    if (products.existsByCategoryId(id))
      throw new IllegalStateException("Danh mục đang có sản phẩm, không thể xóa");
    categories.deleteById(id);
    categories.flush();
  }
}
