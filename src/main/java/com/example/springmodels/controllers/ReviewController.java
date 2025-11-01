package com.example.springmodels.controllers;

import com.example.springmodels.models.*;
import com.example.springmodels.repos.UserRepository;
import com.example.springmodels.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewApiService reviewApiService;

    @Autowired
    private ProductApiService productApiService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
    public String listReviews(Model model) {
        List<Review> reviews = reviewApiService.findAll();
        model.addAttribute("reviews", reviews);
        return "reviews/list";
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
    public String listReviewsByProduct(@PathVariable Long productId, Model model) {
        Product product = productApiService.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Invalid product ID: " + productId);
        }
        List<Review> reviews = reviewApiService.findByProductId(productId);
        model.addAttribute("reviews", reviews);
        model.addAttribute("product", product);
        return "reviews/list-by-product";
    }

    @GetMapping("/create/product/{productId}")
    @PreAuthorize("hasAuthority('USER') and !hasAnyAuthority('MANAGER', 'ADMIN')")
    public String createForm(@PathVariable Long productId, Model model, Authentication authentication) {
        Product product = productApiService.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Invalid product ID: " + productId);
        }
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);

        List<Review> userReviews = reviewApiService.findByUserId(user.getIdUser());
        boolean exists = userReviews.stream().anyMatch(r -> r.getProduct().getId().equals(productId));
        if (exists) {
            model.addAttribute("message", "Вы уже оставили отзыв на этот товар");
            return "redirect:/products/" + productId;
        }

        Review review = new Review();
        review.setProduct(product);
        model.addAttribute("review", review);
        return "reviews/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER') and !hasAnyAuthority('MANAGER', 'ADMIN')")
    public String create(@ModelAttribute Review review, BindingResult bindingResult, Authentication authentication, Model model) {
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);
        review.setUser(user);
        
        Long productId = null;
        if (review.getProduct() != null && review.getProduct().getId() != null) {
            productId = review.getProduct().getId();
            Product product = productApiService.findById(productId);
            if (product != null) {
                review.setProduct(product);
            }
        }
        
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            bindingResult.rejectValue("rating", "error.rating", "Рейтинг должен быть от 1 до 5");
        }
        if (review.getComment() != null && review.getComment().length() > 1000) {
            bindingResult.rejectValue("comment", "error.comment", "Отзыв не должен превышать 1000 символов");
        }
        
        if (bindingResult.hasErrors()) {
            if (productId != null) {
                Product product = productApiService.findById(productId);
                if (product != null) {
                    review.setProduct(product);
                    model.addAttribute("review", review);
                }
            }
            return "reviews/create";
        }
        
        Review createdReview = reviewApiService.create(review);
        if (createdReview == null) {
            model.addAttribute("errorMessage", "Ошибка при создании отзыва. Попробуйте позже.");
            if (productId != null) {
                Product product = productApiService.findById(productId);
                if (product != null) {
                    review.setProduct(product);
                    model.addAttribute("review", review);
                }
            }
            return "reviews/create";
        }
        
        Long redirectProductId = (productId != null) ? productId : createdReview.getProduct().getId();
        return "redirect:/products/" + redirectProductId;
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("(hasAuthority('USER') and !hasAnyAuthority('MANAGER', 'ADMIN')) or hasAuthority('ADMIN')")
    public String editForm(@PathVariable Long id, Model model, Authentication authentication) {
        Review review = reviewApiService.findById(id);
        if (review == null) {
            throw new IllegalArgumentException("Invalid review ID: " + id);
        }
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);

        if (!user.getRoles().contains(RoleEnum.ADMIN) && !review.getUser().getIdUser().equals(user.getIdUser())) {
            return "redirect:/reviews";
        }

        model.addAttribute("review", review);
        return "reviews/edit";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("(hasAuthority('USER') and !hasAnyAuthority('MANAGER', 'ADMIN')) or hasAuthority('ADMIN')")
    public String update(@PathVariable Long id, @ModelAttribute Review review, BindingResult bindingResult, 
                        Authentication authentication, Model model) {
        Review existing = reviewApiService.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Invalid review ID: " + id);
        }
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);

        if (!user.getRoles().contains(RoleEnum.ADMIN) && !existing.getUser().getIdUser().equals(user.getIdUser())) {
            return "redirect:/reviews";
        }

        review.setUser(existing.getUser());
        review.setProduct(existing.getProduct());
        
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            bindingResult.rejectValue("rating", "error.rating", "Рейтинг должен быть от 1 до 5");
        }
        if (review.getComment() != null && review.getComment().length() > 1000) {
            bindingResult.rejectValue("comment", "error.comment", "Отзыв не должен превышать 1000 символов");
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("review", review);
            return "reviews/edit";
        }

        review.setId(id);
        reviewApiService.update(id, review);
        return "redirect:/products/" + review.getProduct().getId();
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("(hasAuthority('USER') and !hasAnyAuthority('MANAGER', 'ADMIN')) or hasAuthority('ADMIN')")
    public String delete(@PathVariable Long id, Authentication authentication) {
        Review review = reviewApiService.findById(id);
        if (review == null) {
            throw new IllegalArgumentException("Invalid review ID: " + id);
        }
        Long productId = review.getProduct().getId();
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);

        if (!user.getRoles().contains(RoleEnum.ADMIN) && !review.getUser().getIdUser().equals(user.getIdUser())) {
            return "redirect:/reviews";
        }

        reviewApiService.delete(id);
        return "redirect:/products/" + productId;
    }
}

