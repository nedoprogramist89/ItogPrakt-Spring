package com.example.springmodels.repos;

import com.example.springmodels.models.ModelUser;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<ModelUser, Long> {
    ModelUser findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
