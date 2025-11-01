package com.example.springmodels.repos;

import com.example.springmodels.models.ShippingAddress;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShippingAddressRepository extends CrudRepository<ShippingAddress, Long> {
    @Query("SELECT s FROM ShippingAddress s WHERE s.user.idUser = :userId")
    List<ShippingAddress> findByUserId(@Param("userId") Long userId);
}

