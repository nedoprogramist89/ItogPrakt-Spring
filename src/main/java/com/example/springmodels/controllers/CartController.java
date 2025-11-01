package com.example.springmodels.controllers;

import com.example.springmodels.models.*;
import com.example.springmodels.repos.UserRepository;
import com.example.springmodels.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartApiService cartApiService;

    @Autowired
    private ProductApiService productApiService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShippingAddressApiService shippingAddressApiService;

    @Autowired
    private OrderApiService orderApiService;

    @Autowired
    private OrderItemApiService orderItemApiService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER') and !hasAnyAuthority('MANAGER', 'ADMIN')")
    public String viewCart(Model model, Authentication authentication) {
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);
        List<CartItem> cartItems = cartApiService.findByUserId(user.getIdUser());
        model.addAttribute("cartItems", cartItems);

        BigDecimal total = BigDecimal.ZERO;
        java.util.Map<Long, BigDecimal> itemTotals = new java.util.HashMap<>();

        for (CartItem item : cartItems) {
            BigDecimal itemTotal = item.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            itemTotals.put(item.getId(), itemTotal);
            total = total.add(itemTotal);
        }

        model.addAttribute("itemTotals", itemTotals);
        model.addAttribute("total", total);

        return "cart/view";
    }

    @PostMapping("/add/{productId}")
    @PreAuthorize("hasAuthority('USER') and !hasAnyAuthority('MANAGER', 'ADMIN')")
    public String addToCart(@PathVariable Long productId, @RequestParam(defaultValue = "1") Integer quantity,
                           Authentication authentication) {
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);
        Product product = productApiService.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Invalid product ID: " + productId);
        }

        List<CartItem> existingItems = cartApiService.findByUserId(user.getIdUser());
        CartItem existingItem = existingItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartApiService.update(existingItem.getId(), existingItem);
        } else {
            CartItem cartItem = new CartItem(user, product, quantity);
            cartApiService.create(cartItem);
        }
        return "redirect:/cart";
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasAuthority('USER') and !hasAnyAuthority('MANAGER', 'ADMIN')")
    public String updateQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        CartItem cartItem = cartApiService.findById(id);
        if (cartItem == null) {
            throw new IllegalArgumentException("Invalid cart item ID: " + id);
        }
        cartItem.setQuantity(quantity);
        cartApiService.update(id, cartItem);
        return "redirect:/cart";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('USER') and !hasAnyAuthority('MANAGER', 'ADMIN')")
    public String removeFromCart(@PathVariable Long id) {
        cartApiService.delete(id);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    @PreAuthorize("hasAuthority('USER') and !hasAnyAuthority('MANAGER', 'ADMIN')")
    public String clearCart(Authentication authentication) {
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);
        cartApiService.clearCart(user.getIdUser());
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    @PreAuthorize("hasAuthority('USER') and !hasAnyAuthority('MANAGER', 'ADMIN')")
    public String checkout(Model model, Authentication authentication) {
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);
        List<CartItem> cartItems = cartApiService.findByUserId(user.getIdUser());

        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            total = total.add(item.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        return "cart/checkout";
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAuthority('USER') and !hasAnyAuthority('MANAGER', 'ADMIN')")
    public String processCheckout(@RequestParam String city,
                                 @RequestParam String street,
                                 @RequestParam String house,
                                 @RequestParam(required = false) String apartment,
                                 @RequestParam(required = false) String postalCode,
                                 @RequestParam(required = false) String comment,
                                 Authentication authentication,
                                 Model model) {
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);

        List<CartItem> cartItems = cartApiService.findByUserId(user.getIdUser());
        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        if (city == null || city.trim().isEmpty()) {
            BigDecimal total = BigDecimal.ZERO;
            for (CartItem item : cartItems) {
                total = total.add(item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())));
            }
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("total", total);
            model.addAttribute("errorMessage", "Город обязателен для заполнения");
            return "cart/checkout";
        }

        if (street == null || street.trim().isEmpty()) {
            BigDecimal total = BigDecimal.ZERO;
            for (CartItem item : cartItems) {
                total = total.add(item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())));
            }
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("total", total);
            model.addAttribute("errorMessage", "Улица обязательна для заполнения");
            return "cart/checkout";
        }

        if (house == null || house.trim().isEmpty()) {
            BigDecimal total = BigDecimal.ZERO;
            for (CartItem item : cartItems) {
                total = total.add(item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())));
            }
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("total", total);
            model.addAttribute("errorMessage", "Дом обязателен для заполнения");
            return "cart/checkout";
        }

        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setCity(city.trim());
        shippingAddress.setStreet(street.trim());
        shippingAddress.setHouse(house.trim());

        if (apartment != null && !apartment.trim().isEmpty()) {
            shippingAddress.setApartment(apartment.trim());
        }

        if (postalCode != null && !postalCode.trim().isEmpty()) {
            shippingAddress.setPostalCode(postalCode.trim());
        }

        shippingAddress.setUser(user);
        ShippingAddress createdAddress = shippingAddressApiService.create(shippingAddress);
        
        if (createdAddress == null) {
            BigDecimal total = BigDecimal.ZERO;
            for (CartItem item : cartItems) {
                total = total.add(item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())));
            }
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("total", total);
            model.addAttribute("errorMessage", "Ошибка при создании адреса доставки");
            return "cart/checkout";
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            totalAmount = totalAmount.add(cartItem.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(createdAddress);
        order.setTotalAmount(totalAmount);

        if (comment != null && !comment.trim().isEmpty()) {
            order.setComment(comment.trim());
        }

        Order createdOrder = orderApiService.create(order);
        
        if (createdOrder == null || createdOrder.getId() == null) {
            BigDecimal total = BigDecimal.ZERO;
            for (CartItem item : cartItems) {
                total = total.add(item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())));
            }
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("total", total);
            model.addAttribute("errorMessage", "Ошибка при создании заказа. Проверьте наличие товаров на складе.");
            return "cart/checkout";
        }

        List<OrderItem> createdItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(createdOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            
            OrderItem createdItem = orderItemApiService.create(orderItem);
            if (createdItem == null) {
                orderApiService.delete(createdOrder.getId());
                BigDecimal total = BigDecimal.ZERO;
                for (CartItem item : cartItems) {
                    total = total.add(item.getProduct().getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity())));
                }
                model.addAttribute("cartItems", cartItems);
                model.addAttribute("total", total);
                model.addAttribute("errorMessage", "Ошибка при создании элементов заказа. Попробуйте позже.");
                return "cart/checkout";
            }
            createdItems.add(createdItem);
        }
        
        cartApiService.clearCart(user.getIdUser());

        return "redirect:/orders/" + createdOrder.getId();
    }
}
