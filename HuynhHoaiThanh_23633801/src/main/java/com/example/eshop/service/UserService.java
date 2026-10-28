package com.example.eshop.service;

import com.example.eshop.dto.*;
import com.example.eshop.entity.*;
import com.example.eshop.exception.NotFoundException;
import com.example.eshop.repository.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository users;
  private final PasswordEncoder encoder;

  public User findByUsername(String username) {
    return users
        .findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));
  }

  public User findById(Long id) {
    return users.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));
  }

  @PreAuthorize("hasRole('SELLER')")
  public List<User> findAll() {
    return users.findAll();
  }

  @Transactional
  public User register(RegisterForm f) {
    if (!f.getPassword().equals(f.getConfirmPassword()))
      throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
    if (users.existsByUsername(f.getUsername()) || users.existsByEmail(f.getEmail()))
      throw new IllegalArgumentException("Tên đăng nhập hoặc email đã tồn tại");
    User u = new User();
    u.setUsername(f.getUsername());
    u.setPassword(encoder.encode(f.getPassword()));
    u.setFullName(f.getFullName());
    u.setEmail(f.getEmail());
    u.setPhone(f.getPhone());
    u.setAddress(f.getAddress());
    u.setRole(Role.ROLE_CUSTOMER);
    u.setEnabled(true);
    return users.saveAndFlush(u);
  }

  @PreAuthorize("hasRole('SELLER')")
  @Transactional
  public User saveByAdmin(UserForm f, String current) {
    User u = f.getId() == null ? new User() : findById(f.getId());
    if (f.getId() == null && users.existsByUsername(f.getUsername()))
      throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
    if (f.getId() != null && !u.getUsername().equals(f.getUsername()))
      throw new IllegalArgumentException("Không được đổi tên đăng nhập");
    if (u.getUsername() != null
        && u.getUsername().equals(current)
        && (!f.isEnabled() || f.getRole() != Role.ROLE_SELLER))
      throw new IllegalArgumentException("Không được tự khóa hoặc hạ quyền tài khoản đang dùng");
    if (f.getId() == null && (f.getPassword() == null || f.getPassword().length() < 6))
      throw new IllegalArgumentException("Mật khẩu tối thiểu 6 ký tự");
    if (f.getPassword() != null && !f.getPassword().isBlank()) {
      if (f.getPassword().length() < 6)
        throw new IllegalArgumentException("Mật khẩu tối thiểu 6 ký tự");
      u.setPassword(encoder.encode(f.getPassword()));
    }
    u.setUsername(f.getUsername());
    u.setFullName(f.getFullName());
    u.setEmail(f.getEmail());
    u.setPhone(f.getPhone());
    u.setAddress(f.getAddress());
    u.setRole(f.getRole());
    u.setEnabled(f.isEnabled());
    return users.saveAndFlush(u);
  }

  @PreAuthorize("hasRole('SELLER')")
  @Transactional
  public void delete(Long id, String current) {
    User u = findById(id);
    if (u.getUsername().equals(current))
      throw new IllegalStateException("Không thể tự xóa tài khoản đang dùng");
    // Keep historical orders while removing the account, also on databases without ON DELETE SET
    // NULL.
    em.createQuery("update Order o set o.user=null where o.user.id=:id")
        .setParameter("id", id)
        .executeUpdate();
    users.delete(u);
    users.flush();
  }

  @jakarta.persistence.PersistenceContext private jakarta.persistence.EntityManager em;
}
