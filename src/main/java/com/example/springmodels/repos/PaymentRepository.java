package com.example.springmodels.repos;

import com.example.springmodels.models.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
    Payment findByOrderId(Long orderId);
    List<Payment> findByStatus(Payment.PaymentStatus status);
}

