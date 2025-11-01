package com.example.springmodels.repos;

import com.example.springmodels.models.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.user.idUser = :userId")
    List<Order> findByUserId(@Param("userId") Long userId);
    
    List<Order> findByStatus(Order.OrderStatus status);
}

