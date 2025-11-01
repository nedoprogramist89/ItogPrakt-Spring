package com.example.springmodels.controllers;

import com.example.springmodels.models.Product;
import com.example.springmodels.service.ProductApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ProductApiService productApiService;

    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
    public String home(Model model) {
        List<Product> allProducts = productApiService.findAll();
        List<Product> products = allProducts.stream()
                .limit(6)
                .collect(Collectors.toList());
        model.addAttribute("products", products);
        return "index";
    }
}

