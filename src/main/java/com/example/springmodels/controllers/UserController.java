package com.example.springmodels.controllers;

import com.example.springmodels.models.ModelUser;
import com.example.springmodels.models.RoleEnum;
import com.example.springmodels.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public String userView(Model model) {
        model.addAttribute("user_list", userRepository.findAll());
        return "admin/users";
    }

    @GetMapping("/{id}")
    public String detailView(@PathVariable Long id, Model model) {
        ModelUser user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        model.addAttribute("user_object", user);
        return "info";
    }

    @GetMapping("/{id}/update")
    public String updView(@PathVariable Long id, Model model) {
        ModelUser user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        model.addAttribute("user_object", user);
        model.addAttribute("roles", RoleEnum.values());
        return "update";
    }

    @PostMapping("/{id}/update")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String username,
                             @RequestParam(required = false) String email,
                             @RequestParam(required = false) String fullName,
                             @RequestParam(required = false) Boolean active,
                             @RequestParam(name = "roles[]", required = false) String[] roles,
                             HttpServletRequest request) {
        ModelUser user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        user.setUsername(username);
        
        if (email != null && !email.isEmpty()) {
            user.setEmail(email);
        }
        if (fullName != null && !fullName.isEmpty()) {
            user.setFullName(fullName);
        }
        // Обрабатываем чекбокс active (если не передан, значит false)
        String activeParam = request.getParameter("active");
        user.setActive("true".equals(activeParam));

        user.getRoles().clear();
        if (roles != null) {
            for (String role : roles) {
                user.getRoles().add(RoleEnum.valueOf(role));
            }
        } else {
            // Если роли не выбраны, устанавливаем USER по умолчанию
            user.getRoles().add(RoleEnum.USER);
        }

        userRepository.save(user);
        return "redirect:/admin/" + id;
    }
}
