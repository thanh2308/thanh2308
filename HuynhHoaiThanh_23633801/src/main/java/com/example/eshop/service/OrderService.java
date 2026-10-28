package com.example.eshop.service;

import com.example.eshop.cart.CartItem;
import com.example.eshop.dto.CheckoutForm;
import com.example.eshop.entity.*;
import com.example.eshop.entity.Order;
import com.example.eshop.exception.NotFoundException;
import com.example.eshop.repository.*;
import java.math.BigDecimal;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orders;
  private final ProductRepository products;
  private final UserService users;

  public Order findById(Long id) {
    return orders.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng"));
  }

  @PreAuthorize("hasRole('CUSTOMER')")
  public Order getOwnedOrder(Long id, String username) {
    Order o = findById(id);
    if (o.getUser() == null || !o.getUser().getUsername().equals(username))
      throw new AccessDeniedException("Đơn hàng không thuộc tài khoản này");
    return o;
  }

  @PreAuthorize("hasRole('CUSTOMER')")
  public List<Order> myOrders(String username) {
    return orders.findByUserUsernameOrderByOrderDateDesc(username);
  }

  @PreAuthorize("hasRole('SELLER')")
  public List<Order> findAll() {
    return orders.findAllByOrderByOrderDateDesc();
  }

  @PreAuthorize("hasRole('CUSTOMER')")
  @Transactional
  public Order placeOrder(CheckoutForm f, List<CartItem> items, String username) {
    if (items.isEmpty()) throw new IllegalArgumentException("Giỏ hàng trống");
    Order o = new Order();
    o.setCustomerName(f.getCustomerName());
    o.setPhone(f.getPhone());
    o.setAddress(f.getAddress());
    o.setNote(f.getNote());
    o.setPaymentMethod(f.getPaymentMethod());
    o.setUser(users.findByUsername(username));
    BigDecimal total = BigDecimal.ZERO;
    for (CartItem i :
        items.stream().sorted(Comparator.comparing(CartItem::getProductId)).toList()) {
      Product p =
          products
              .findLockedById(i.getProductId())
              .orElseThrow(() -> new NotFoundException("Sản phẩm không còn tồn tại"));
      if (i.getQuantity() < 1 || p.getQuantity() < i.getQuantity())
        throw new IllegalArgumentException("Không đủ tồn kho: " + p.getName());
      p.setQuantity(p.getQuantity() - i.getQuantity());
      OrderDetail d = new OrderDetail();
      d.setProduct(p);
      d.setQuantity(i.getQuantity());
      d.setUnitPrice(p.getPrice());
      o.addDetail(d);
      total = total.add(d.getSubtotal());
    }
    o.setTotalAmount(total);
    return orders.saveAndFlush(o);
  }

  @PreAuthorize("hasRole('CUSTOMER')")
  @Transactional
  public void confirmPayment(Long id, String username) {
    Order o =
        orders
            .findLockedById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng"));
    if (o.getUser() == null || !o.getUser().getUsername().equals(username))
      throw new AccessDeniedException("Không có quyền");
    if (o.getPaymentMethod() != PaymentMethod.BANKING || o.getStatus() != OrderStatus.NEW)
      throw new IllegalStateException("Đơn không chờ chuyển khoản");
    o.setStatus(OrderStatus.PAID);
  }

  @PreAuthorize("hasRole('SELLER')")
  @Transactional
  public void updateStatus(Long id, OrderStatus status) {
    Order o =
        orders
            .findLockedById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng"));
    if (status == o.getStatus()) return;
    boolean allowed =
        switch (o.getStatus()) {
          case NEW ->
              status == OrderStatus.PAID
                  || status == OrderStatus.SHIPPING
                  || status == OrderStatus.CANCELLED;
          case PAID -> status == OrderStatus.SHIPPING || status == OrderStatus.CANCELLED;
          case SHIPPING -> status == OrderStatus.DONE;
          case DONE, CANCELLED -> false;
        };
    if (!allowed) throw new IllegalStateException("Chuyển trạng thái không hợp lệ");
    if (status == OrderStatus.CANCELLED) restore(o);
    o.setStatus(status);
  }

  private void restore(Order o) {
    for (OrderDetail d :
        o.getDetails().stream()
            .sorted(Comparator.comparing(x -> x.getProduct().getId()))
            .toList()) {
      Product p = products.findLockedById(d.getProduct().getId()).orElseThrow();
      p.setQuantity(Math.addExact(p.getQuantity(), d.getQuantity()));
    }
  }

  @PreAuthorize("hasRole('SELLER')")
  @Transactional
  public void delete(Long id) {
    Order o =
        orders
            .findLockedById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng"));
    if (o.getStatus() != OrderStatus.CANCELLED && o.getStatus() != OrderStatus.DONE)
      throw new IllegalStateException("Chỉ xóa đơn đã hoàn thành hoặc đã hủy");
    orders.delete(o);
    orders.flush();
  }
}
