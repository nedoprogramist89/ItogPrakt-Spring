package com.example.springmodels.repos;

import com.example.springmodels.models.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByManufacturerId(Long manufacturerId);
    List<Product> findByNameContainingIgnoreCase(String name);
}

