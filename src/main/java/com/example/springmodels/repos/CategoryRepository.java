package com.example.springmodels.repos;

import com.example.springmodels.models.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    Category findByName(String name);
    boolean existsByName(String name);
}

