package com.example.springmodels.service;

import com.example.springmodels.config.ApiProperties;
import com.example.springmodels.models.Payment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentApiService {

    private final RestTemplate restTemplate;
    private final ApiProperties apiProperties;

    public PaymentApiService(RestTemplate restTemplate, ApiProperties apiProperties) {
        this.restTemplate = restTemplate;
        this.apiProperties = apiProperties;
    }

    private String getBaseUrl() {
        return apiProperties.getBaseUrl() + "/payments";
    }

    public List<Payment> findAll() {
        try {
            ResponseEntity<List<Payment>> response = restTemplate.exchange(
                    getBaseUrl(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Payment>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Payment findById(Long id) {
        try {
            ResponseEntity<Payment> response = restTemplate.getForEntity(
                    getBaseUrl() + "/" + id,
                    Payment.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Payment findByOrderId(Long orderId) {
        try {
            ResponseEntity<Payment> response = restTemplate.getForEntity(
                    getBaseUrl() + "/order/" + orderId,
                    Payment.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Payment create(Payment payment) {
        try {
            com.example.springmodels.models.Order originalOrder = payment.getOrder();
            
            payment.setId(null);
            payment.setPaymentDate(null);
            
            com.example.springmodels.models.Order orderForApi = null;
            if (originalOrder != null && originalOrder.getId() != null) {
                orderForApi = new com.example.springmodels.models.Order();
                orderForApi.setId(originalOrder.getId());
                orderForApi.setTotalAmount(originalOrder.getTotalAmount() != null ? originalOrder.getTotalAmount() : java.math.BigDecimal.ZERO);
                orderForApi.setStatus(originalOrder.getStatus() != null ? originalOrder.getStatus() : com.example.springmodels.models.Order.OrderStatus.PENDING);
                if (originalOrder.getUser() != null) {
                    orderForApi.setUser(originalOrder.getUser());
                }
                if (originalOrder.getShippingAddress() != null) {
                    orderForApi.setShippingAddress(originalOrder.getShippingAddress());
                }
            }
            
            payment.setOrder(orderForApi);
            
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            
            HttpEntity<Payment> request = new HttpEntity<>(payment, headers);
            
            ResponseEntity<Payment> response = restTemplate.postForEntity(
                    getBaseUrl(),
                    request,
                    Payment.class
            );
            
            payment.setOrder(originalOrder);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public Payment update(Long id, Payment payment) {
        try {
            payment.setId(id);
            HttpEntity<Payment> request = new HttpEntity<>(payment);
            ResponseEntity<Payment> response = restTemplate.exchange(
                    getBaseUrl() + "/" + id,
                    HttpMethod.PUT,
                    request,
                    Payment.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean delete(Long id) {
        try {
            restTemplate.delete(getBaseUrl() + "/" + id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

