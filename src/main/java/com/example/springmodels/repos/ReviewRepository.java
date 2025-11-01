package com.example.springmodels.repos;

import com.example.springmodels.models.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    
    @Query("SELECT r FROM Review r WHERE r.user.idUser = :userId")
    List<Review> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Review r WHERE r.user.idUser = :userId AND r.product.id = :productId")
    boolean existsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}

