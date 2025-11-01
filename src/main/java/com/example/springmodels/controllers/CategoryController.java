package com.example.springmodels.controllers;

import com.example.springmodels.models.Category;
import com.example.springmodels.service.CategoryApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryApiService categoryApiService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
    public String listCategories(Model model) {
        List<Category> categories = categoryApiService.findAll();
        model.addAttribute("categories", categories);
        return "categories/list";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
    public String viewCategory(@PathVariable Long id, Model model) {
        Category category = categoryApiService.findById(id);
        if (category == null) {
            throw new IllegalArgumentException("Invalid category ID: " + id);
        }
        model.addAttribute("category", category);
        return "categories/view";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public String createForm(Model model) {
        model.addAttribute("category", new Category());
        return "categories/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public String create(@Valid @ModelAttribute Category category, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "categories/create";
        }
        // Проверка уникальности имени через API
        List<Category> existing = categoryApiService.findAll();
        for (Category c : existing) {
            if (c.getName().equals(category.getName())) {
                bindingResult.rejectValue("name", "error.name", "Категория с таким названием уже существует");
                return "categories/create";
            }
        }
        categoryApiService.create(category);
        return "redirect:/categories";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        Category category = categoryApiService.findById(id);
        if (category == null) {
            throw new IllegalArgumentException("Invalid category ID: " + id);
        }
        model.addAttribute("category", category);
        return "categories/edit";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Category category, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "categories/edit";
        }
        Category existingCategory = categoryApiService.findById(id);
        if (existingCategory == null) {
            throw new IllegalArgumentException("Invalid category ID: " + id);
        }
        // Проверка уникальности имени через API
        if (!existingCategory.getName().equals(category.getName())) {
            List<Category> all = categoryApiService.findAll();
            for (Category c : all) {
                if (c.getName().equals(category.getName())) {
                    bindingResult.rejectValue("name", "error.name", "Категория с таким названием уже существует");
                    return "categories/edit";
                }
            }
        }
        categoryApiService.update(id, category);
        return "redirect:/categories/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String delete(@PathVariable Long id) {
        categoryApiService.delete(id);
        return "redirect:/categories";
    }
}

