package com.example.springmodels.controllers;

import com.example.springmodels.models.ModelUser;
import com.example.springmodels.models.RoleEnum;
import com.example.springmodels.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Collections;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/registration")
    public String regView(Model model) {
        model.addAttribute("user", new ModelUser());
        return "regis";
    }

    @PostMapping("/registration")
    public String reg(@Valid ModelUser user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "regis";
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("message", "Пользователь с таким логином уже существует");
            model.addAttribute("user", user);
            return "regis";
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("message", "Пользователь с таким email уже существует");
            model.addAttribute("user", user);
            return "regis";
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(RoleEnum.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login";
    }
}
