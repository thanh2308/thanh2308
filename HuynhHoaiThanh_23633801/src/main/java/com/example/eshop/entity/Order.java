package com.example.eshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreationTimestamp
  @Column(name = "order_date", updatable = false)
  private LocalDateTime orderDate;

  @Column(name = "customer_name", nullable = false, length = 100)
  private String customerName;

  @Column(nullable = false, length = 20)
  private String phone;

  @Column(nullable = false)
  private String address;

  @Column(length = 500)
  private String note;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_method", nullable = false, length = 20)
  private PaymentMethod paymentMethod;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private OrderStatus status = OrderStatus.NEW;

  @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
  private BigDecimal totalAmount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private java.util.List<OrderDetail> details = new java.util.ArrayList<>();

  public void addDetail(OrderDetail d) {
    d.setOrder(this);
    details.add(d);
  }
}
