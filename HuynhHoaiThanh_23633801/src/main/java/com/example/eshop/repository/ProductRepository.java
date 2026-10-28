package com.example.eshop.repository;

import com.example.eshop.entity.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
  boolean existsByCategoryId(Long id);

  @Query(
      "select p from Product p where (:keyword is null or lower(p.name) like"
          + " lower(concat('%',:keyword,'%'))) and (:categoryId is null or"
          + " p.category.id=:categoryId)")
  Page<Product> search(
      @Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);

  @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @Query("select p from Product p where p.id=:id")
  Optional<Product> findLockedById(@Param("id") Long id);

  @Query("select count(d)>0 from OrderDetail d where d.product.id=:id")
  boolean usedInOrder(@Param("id") Long id);
}
