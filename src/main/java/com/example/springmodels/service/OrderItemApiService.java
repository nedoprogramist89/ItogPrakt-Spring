package com.example.springmodels.service;

import com.example.springmodels.config.ApiProperties;
import com.example.springmodels.models.OrderItem;
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
public class OrderItemApiService {

    private final RestTemplate restTemplate;
    private final ApiProperties apiProperties;

    public OrderItemApiService(RestTemplate restTemplate, ApiProperties apiProperties) {
        this.restTemplate = restTemplate;
        this.apiProperties = apiProperties;
    }

    private String getBaseUrl() {
        return apiProperties.getBaseUrl() + "/order-items";
    }

    public List<OrderItem> findAll() {
        try {
            ResponseEntity<List<OrderItem>> response = restTemplate.exchange(
                    getBaseUrl(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<OrderItem>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<OrderItem> findByOrderId(Long orderId) {
        try {
            ResponseEntity<List<OrderItem>> response = restTemplate.exchange(
                    getBaseUrl() + "/order/" + orderId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<OrderItem>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public OrderItem findById(Long id) {
        try {
            ResponseEntity<OrderItem> response = restTemplate.getForEntity(
                    getBaseUrl() + "/" + id,
                    OrderItem.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public OrderItem create(OrderItem orderItem) {
        try {
            HttpEntity<OrderItem> request = new HttpEntity<>(orderItem);
            ResponseEntity<OrderItem> response = restTemplate.postForEntity(
                    getBaseUrl(),
                    request,
                    OrderItem.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public OrderItem update(Long id, OrderItem orderItem) {
        try {
            orderItem.setId(id);
            HttpEntity<OrderItem> request = new HttpEntity<>(orderItem);
            ResponseEntity<OrderItem> response = restTemplate.exchange(
                    getBaseUrl() + "/" + id,
                    HttpMethod.PUT,
                    request,
                    OrderItem.class
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

