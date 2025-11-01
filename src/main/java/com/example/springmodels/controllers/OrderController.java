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
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderApiService orderApiService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShippingAddressApiService shippingAddressApiService;

    @Autowired
    private PaymentApiService paymentApiService;

    @Autowired
    private OrderItemApiService orderItemApiService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
    public String listOrders(Model model, Authentication authentication) {
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);
        List<Order> orders;
        boolean isManagerOrAdmin = user.getRoles().contains(RoleEnum.ADMIN) || user.getRoles().contains(RoleEnum.MANAGER);

        if (isManagerOrAdmin) {
            orders = orderApiService.findAll();
        } else {
            orders = orderApiService.findByUserId(user.getIdUser());
        }

        model.addAttribute("orders", orders);
        model.addAttribute("isManagerOrAdmin", isManagerOrAdmin);
        return "orders/list";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
    public String viewOrder(@PathVariable Long id, Model model, Authentication authentication) {
        Order order = orderApiService.findById(id);
        if (order == null) {
            throw new IllegalArgumentException("Invalid order ID: " + id);
        }

        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);

        if (!user.getRoles().contains(RoleEnum.ADMIN) && !user.getRoles().contains(RoleEnum.MANAGER)
                && !order.getUser().getIdUser().equals(user.getIdUser())) {
            return "redirect:/orders";
        }

        List<OrderItem> orderItems = orderItemApiService.findByOrderId(order.getId());
        order.setOrderItems(orderItems);
        
        Payment payment = paymentApiService.findByOrderId(order.getId());
        
        boolean isManagerOrAdmin = user.getRoles().contains(RoleEnum.ADMIN) || user.getRoles().contains(RoleEnum.MANAGER);
        boolean isRegularUser = !isManagerOrAdmin;
        
        model.addAttribute("order", order);
        model.addAttribute("payment", payment);
        model.addAttribute("isRegularUser", isRegularUser);
        model.addAttribute("hasManagerRole", user.getRoles().contains(RoleEnum.MANAGER));
        model.addAttribute("hasAdminRole", user.getRoles().contains(RoleEnum.ADMIN));
        return "orders/view";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAuthority('USER') and !hasAnyAuthority('MANAGER', 'ADMIN')")
    public String createForm(Model model, Authentication authentication) {
        return "orders/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER') and !hasAnyAuthority('MANAGER', 'ADMIN')")
    public String create(@RequestParam String city,
                        @RequestParam String street,
                        @RequestParam String house,
                        @RequestParam(required = false) String apartment,
                        @RequestParam(required = false) String postalCode,
                        @RequestParam(required = false) String comment,
                        Authentication authentication,
                        Model model) {
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);

        if (city == null || city.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Город обязателен для заполнения");
            return "orders/create";
        }

        if (street == null || street.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Улица обязательна для заполнения");
            return "orders/create";
        }

        if (house == null || house.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Дом обязателен для заполнения");
            return "orders/create";
        }

        // Создаем адрес доставки через API
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
        shippingAddressApiService.create(shippingAddress);

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setTotalAmount(BigDecimal.ZERO);

        if (comment != null && !comment.trim().isEmpty()) {
            order.setComment(comment.trim());
        }

        orderApiService.create(order);
        return "redirect:/orders";
    }

    @PostMapping("/{id}/update-status")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public String updateStatus(@PathVariable Long id, @RequestParam Order.OrderStatus status) {
        Order order = orderApiService.findById(id);
        if (order == null) {
            throw new IllegalArgumentException("Invalid order ID: " + id);
        }
        order.setStatus(status);
        orderApiService.update(id, order);
        return "redirect:/orders/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String delete(@PathVariable Long id) {
        orderApiService.delete(id);
        return "redirect:/orders";
    }
}
