package com.example.eshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 150)
  @Column(nullable = false, length = 150)
  private String name;

  @Size(max = 500)
  @Column(length = 500)
  private String description;

  @NotNull
  @DecimalMin("0")
  @Digits(integer = 13, fraction = 2)
  @Column(precision = 15, scale = 2, nullable = false)
  private BigDecimal price;

  @NotNull
  @Min(0)
  @Column(nullable = false)
  private Integer quantity;

  @Size(max = 500)
  @Column(name = "image_url", length = 500)
  private String imageUrl;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;
}
