package com.example.springmodels.repos;

import com.example.springmodels.models.CartItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends CrudRepository<CartItem, Long> {
    @Query("SELECT c FROM CartItem c WHERE c.user.idUser = :userId")
    List<CartItem> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT c FROM CartItem c WHERE c.user.idUser = :userId AND c.product.id = :productId")
    Optional<CartItem> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.user.idUser = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}

