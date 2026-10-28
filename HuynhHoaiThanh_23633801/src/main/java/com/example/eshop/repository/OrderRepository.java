package com.example.eshop.repository;

import com.example.eshop.entity.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findByUserUsernameOrderByOrderDateDesc(String username);

  List<Order> findAllByOrderByOrderDateDesc();

  @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @Query("select o from Order o where o.id=:id")
  Optional<Order> findLockedById(@Param("id") Long id);
}
