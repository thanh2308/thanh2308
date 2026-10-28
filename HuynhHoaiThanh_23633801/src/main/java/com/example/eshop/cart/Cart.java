package com.example.eshop.cart;

import com.example.eshop.entity.Product;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class Cart implements java.io.Serializable {
  private final Map<Long, CartItem> lines = new LinkedHashMap<>();

  public synchronized void add(Product p, int quantity) {
    if (quantity < 1) throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
    int old = lines.containsKey(p.getId()) ? lines.get(p.getId()).getQuantity() : 0;
    if ((long) old + quantity > p.getQuantity())
      throw new IllegalArgumentException("Không đủ tồn kho");
    lines.put(
        p.getId(),
        new CartItem(p.getId(), p.getName(), p.getImageUrl(), p.getPrice(), old + quantity));
  }

  public synchronized void update(Product p, int quantity) {
    if (quantity < 0) throw new IllegalArgumentException("Số lượng không được âm");
    if (quantity == 0) {
      remove(p.getId());
      return;
    }
    if (!lines.containsKey(p.getId()))
      throw new IllegalArgumentException("Sản phẩm không có trong giỏ");
    if (quantity > p.getQuantity()) throw new IllegalArgumentException("Không đủ tồn kho");
    lines.put(
        p.getId(), new CartItem(p.getId(), p.getName(), p.getImageUrl(), p.getPrice(), quantity));
  }

  public synchronized void remove(Long id) {
    lines.remove(id);
  }

  public synchronized void clear() {
    lines.clear();
  }

  public synchronized java.util.List<CartItem> getItems() {
    return lines.values().stream()
        .map(
            i ->
                new CartItem(
                    i.getProductId(), i.getName(), i.getImageUrl(), i.getPrice(), i.getQuantity()))
        .toList();
  }

  public synchronized boolean isEmpty() {
    return lines.isEmpty();
  }

  public synchronized int getTotalQuantity() {
    return lines.values().stream().mapToInt(CartItem::getQuantity).sum();
  }

  public synchronized BigDecimal getTotalAmount() {
    return lines.values().stream()
        .map(CartItem::getSubtotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
