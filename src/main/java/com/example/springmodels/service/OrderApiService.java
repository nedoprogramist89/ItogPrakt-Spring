package com.example.springmodels.service;

import com.example.springmodels.config.ApiProperties;
import com.example.springmodels.models.Order;
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
public class OrderApiService {

    private final RestTemplate restTemplate;
    private final ApiProperties apiProperties;

    public OrderApiService(RestTemplate restTemplate, ApiProperties apiProperties) {
        this.restTemplate = restTemplate;
        this.apiProperties = apiProperties;
    }

    private String getBaseUrl() {
        return apiProperties.getBaseUrl() + "/orders";
    }

    public List<Order> findAll() {
        try {
            ResponseEntity<List<Order>> response = restTemplate.exchange(
                    getBaseUrl(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Order>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Order> findByUserId(Long userId) {
        try {
            ResponseEntity<List<Order>> response = restTemplate.exchange(
                    getBaseUrl() + "/user/" + userId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Order>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Order findById(Long id) {
        try {
            ResponseEntity<Order> response = restTemplate.getForEntity(
                    getBaseUrl() + "/" + id,
                    Order.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Order create(Order order) {
        try {
            HttpEntity<Order> request = new HttpEntity<>(order);
            ResponseEntity<Order> response = restTemplate.postForEntity(
                    getBaseUrl(),
                    request,
                    Order.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public Order update(Long id, Order order) {
        try {
            order.setId(id);
            HttpEntity<Order> request = new HttpEntity<>(order);
            ResponseEntity<Order> response = restTemplate.exchange(
                    getBaseUrl() + "/" + id,
                    HttpMethod.PUT,
                    request,
                    Order.class
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

