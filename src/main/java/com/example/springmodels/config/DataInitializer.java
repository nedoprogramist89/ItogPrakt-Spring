package com.example.springmodels.config;

import com.example.springmodels.models.ModelUser;
import com.example.springmodels.models.RoleEnum;
import com.example.springmodels.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByUsername("admin")) {
            ModelUser admin = new ModelUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setFullName("Администратор");
            admin.setActive(true);
            Set<RoleEnum> adminRoles = new HashSet<>();
            adminRoles.add(RoleEnum.ADMIN);
            admin.setRoles(adminRoles);
            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("manager")) {
            ModelUser manager = new ModelUser();
            manager.setUsername("manager");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setEmail("manager@example.com");
            manager.setFullName("Менеджер");
            manager.setActive(true);
            Set<RoleEnum> managerRoles = new HashSet<>();
            managerRoles.add(RoleEnum.MANAGER);
            manager.setRoles(managerRoles);
            userRepository.save(manager);
        }

        if (!userRepository.existsByUsername("user")) {
            ModelUser user = new ModelUser();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@example.com");
            user.setFullName("Пользователь");
            user.setActive(true);
            Set<RoleEnum> userRoles = new HashSet<>();
            userRoles.add(RoleEnum.USER);
            user.setRoles(userRoles);
            userRepository.save(user);
        }
    }
}

