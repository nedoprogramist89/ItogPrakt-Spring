package com.example.springmodels.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collection;
import java.util.stream.Collectors;

@ControllerAdvice
public class ThymeleafControllerAdvice {

    @ModelAttribute
    public void addRoleFlags(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            
            // Получаем все роли как список строк
            Collection<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            
            // Проверяем наличие ролей
            boolean hasUser = roles.contains("USER");
            boolean hasManager = roles.contains("MANAGER");
            boolean hasAdmin = roles.contains("ADMIN");
            
            // Флаг для обычного пользователя (только USER, без MANAGER и ADMIN)
            boolean isRegularUser = hasUser && !hasManager && !hasAdmin;
            
            // Добавляем флаги в модель для всех шаблонов
            model.addAttribute("isRegularUser", isRegularUser);
            model.addAttribute("hasUserRole", hasUser);
            model.addAttribute("hasManagerRole", hasManager);
            model.addAttribute("hasAdminRole", hasAdmin);
        } else {
            model.addAttribute("isRegularUser", false);
            model.addAttribute("hasUserRole", false);
            model.addAttribute("hasManagerRole", false);
            model.addAttribute("hasAdminRole", false);
        }
    }
}

