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
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentApiService paymentApiService;

    @Autowired
    private OrderApiService orderApiService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public String listPayments(Model model) {
        List<Payment> payments = paymentApiService.findAll();
        model.addAttribute("payments", payments);
        return "payments/list";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
    public String viewPayment(@PathVariable Long id, Model model, Authentication authentication) {
        Payment payment = paymentApiService.findById(id);
        if (payment == null) {
            throw new IllegalArgumentException("Invalid payment ID: " + id);
        }
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);

        if (!user.getRoles().contains(RoleEnum.ADMIN) && !user.getRoles().contains(RoleEnum.MANAGER)
                && !payment.getOrder().getUser().getIdUser().equals(user.getIdUser())) {
            return "redirect:/payments";
        }

        model.addAttribute("payment", payment);
        return "payments/view";
    }

    @GetMapping("/order/{orderId}/create")
    @PreAuthorize("hasAuthority('USER') and !hasAnyAuthority('MANAGER', 'ADMIN')")
    public String createForm(@PathVariable Long orderId, Model model) {
        Order order = orderApiService.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Invalid order ID: " + orderId);
        }
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        model.addAttribute("payment", payment);
        Payment.PaymentMethod[] methods = Payment.PaymentMethod.values();
        model.addAttribute("paymentMethods", methods);
        return "payments/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER') and !hasAnyAuthority('MANAGER', 'ADMIN')")
    public String create(@ModelAttribute Payment payment, BindingResult bindingResult, Model model) {
        Long orderId = null;
        if (payment.getOrder() != null && payment.getOrder().getId() != null) {
            orderId = payment.getOrder().getId();
            Order order = orderApiService.findById(orderId);
            if (order != null) {
                payment.setOrder(order);
                if (payment.getAmount() == null && order.getTotalAmount() != null) {
                    payment.setAmount(order.getTotalAmount());
                }
            }
        }
        
        if (payment.getAmount() == null) {
            bindingResult.rejectValue("amount", "error.amount", "Сумма обязательна");
        } else if (payment.getAmount().compareTo(java.math.BigDecimal.valueOf(0.01)) < 0) {
            bindingResult.rejectValue("amount", "error.amount", "Сумма должна быть больше 0");
        }
        if (payment.getPaymentMethod() == null) {
            bindingResult.rejectValue("paymentMethod", "error.paymentMethod", "Метод оплаты обязателен");
        }
        if (payment.getOrder() == null) {
            bindingResult.rejectValue("order", "error.order", "Заказ обязателен");
        }
        
        if (bindingResult.hasErrors()) {
            Payment.PaymentMethod[] methods = Payment.PaymentMethod.values();
            model.addAttribute("paymentMethods", methods);
            if (orderId != null) {
                Order order = orderApiService.findById(orderId);
                if (order != null) {
                    payment.setOrder(order);
                    if (payment.getAmount() == null) {
                        payment.setAmount(order.getTotalAmount());
                    }
                }
            }
            model.addAttribute("payment", payment);
            return "payments/create";
        }
        
        Payment createdPayment = paymentApiService.create(payment);
        if (createdPayment == null || createdPayment.getId() == null) {
            model.addAttribute("errorMessage", "Ошибка при создании платежа. Попробуйте позже.");
            Payment.PaymentMethod[] methods = Payment.PaymentMethod.values();
            model.addAttribute("paymentMethods", methods);
            if (payment.getOrder() != null && payment.getOrder().getId() != null) {
                Order order = orderApiService.findById(payment.getOrder().getId());
                if (order != null) {
                    payment.setOrder(order);
                    model.addAttribute("payment", payment);
                }
            }
            return "payments/create";
        }
        
        return "redirect:/payments/" + createdPayment.getId();
    }

    @PostMapping("/{id}/update-status")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public String updateStatus(@PathVariable Long id, @RequestParam Payment.PaymentStatus status) {
        Payment payment = paymentApiService.findById(id);
        if (payment == null) {
            throw new IllegalArgumentException("Invalid payment ID: " + id);
        }
        payment.setStatus(status);
        paymentApiService.update(id, payment);
        return "redirect:/payments/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String delete(@PathVariable Long id) {
        paymentApiService.delete(id);
        return "redirect:/payments";
    }
}

