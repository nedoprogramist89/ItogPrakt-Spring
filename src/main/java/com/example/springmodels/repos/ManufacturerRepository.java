package com.example.springmodels.repos;

import com.example.springmodels.models.Manufacturer;
import org.springframework.data.repository.CrudRepository;

public interface ManufacturerRepository extends CrudRepository<Manufacturer, Long> {
    Manufacturer findByName(String name);
    boolean existsByName(String name);
}

