package com.example.springmodels.controllers;

import com.example.springmodels.models.*;
import com.example.springmodels.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductApiService productApiService;

    @Autowired
    private CategoryApiService categoryApiService;

    @Autowired
    private ManufacturerApiService manufacturerApiService;

    @Autowired
    private ReviewApiService reviewApiService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
    public String listProducts(Model model) {
        List<Product> products = productApiService.findAll();
        model.addAttribute("products", products);
        return "products/list";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productApiService.findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Invalid product ID: " + id);
        }
        List<Review> reviews = reviewApiService.findByProductId(id);
        product.setReviews(reviews);
        model.addAttribute("product", product);
        return "products/view";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryApiService.findAll());
        model.addAttribute("manufacturers", manufacturerApiService.findAll());
        return "products/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public String create(@ModelAttribute Product product, 
                        @RequestParam Long categoryId, @RequestParam Long manufacturerId,
                        BindingResult bindingResult, Model model) {
        if (categoryId != null) {
            product.setCategory(categoryApiService.findById(categoryId));
        }
        if (manufacturerId != null) {
            product.setManufacturer(manufacturerApiService.findById(manufacturerId));
        }
        
        if (product.getCategory() == null) {
            bindingResult.rejectValue("category", "error.category", "Категория обязательна");
        }
        if (product.getManufacturer() == null) {
            bindingResult.rejectValue("manufacturer", "error.manufacturer", "Производитель обязателен");
        }
        
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            bindingResult.rejectValue("name", "error.name", "Название товара не может быть пустым");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            bindingResult.rejectValue("price", "error.price", "Цена должна быть больше 0");
        }
        if (product.getQuantity() == null || product.getQuantity() < 0) {
            bindingResult.rejectValue("quantity", "error.quantity", "Количество не может быть отрицательным");
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryApiService.findAll());
            model.addAttribute("manufacturers", manufacturerApiService.findAll());
            return "products/create";
        }
        
        productApiService.create(product);
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productApiService.findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Invalid product ID: " + id);
        }
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryApiService.findAll());
        model.addAttribute("manufacturers", manufacturerApiService.findAll());
        return "products/edit";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public String update(@PathVariable Long id, @ModelAttribute Product product, 
                        @RequestParam Long categoryId, @RequestParam Long manufacturerId,
                        BindingResult bindingResult, Model model) {
        if (categoryId != null) {
            product.setCategory(categoryApiService.findById(categoryId));
        }
        if (manufacturerId != null) {
            product.setManufacturer(manufacturerApiService.findById(manufacturerId));
        }
        
        if (product.getCategory() == null) {
            bindingResult.rejectValue("category", "error.category", "Категория обязательна");
        }
        if (product.getManufacturer() == null) {
            bindingResult.rejectValue("manufacturer", "error.manufacturer", "Производитель обязателен");
        }
        
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            bindingResult.rejectValue("name", "error.name", "Название товара не может быть пустым");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            bindingResult.rejectValue("price", "error.price", "Цена должна быть больше 0");
        }
        if (product.getQuantity() == null || product.getQuantity() < 0) {
            bindingResult.rejectValue("quantity", "error.quantity", "Количество не может быть отрицательным");
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryApiService.findAll());
            model.addAttribute("manufacturers", manufacturerApiService.findAll());
            return "products/edit";
        }
        
        if (productApiService.findById(id) == null) {
            throw new IllegalArgumentException("Invalid product ID: " + id);
        }
        productApiService.update(id, product);
        return "redirect:/products/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String delete(@PathVariable Long id) {
        productApiService.delete(id);
        return "redirect:/products";
    }
}

