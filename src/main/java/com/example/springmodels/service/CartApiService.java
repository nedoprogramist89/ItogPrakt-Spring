package com.example.springmodels.service;

import com.example.springmodels.config.ApiProperties;
import com.example.springmodels.models.CartItem;
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
public class CartApiService {

    private final RestTemplate restTemplate;
    private final ApiProperties apiProperties;

    public CartApiService(RestTemplate restTemplate, ApiProperties apiProperties) {
        this.restTemplate = restTemplate;
        this.apiProperties = apiProperties;
    }

    private String getBaseUrl() {
        return apiProperties.getBaseUrl() + "/cart";
    }

    public List<CartItem> findByUserId(Long userId) {
        try {
            ResponseEntity<List<CartItem>> response = restTemplate.exchange(
                    getBaseUrl() + "/user/" + userId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CartItem>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public CartItem findById(Long id) {
        try {
            ResponseEntity<CartItem> response = restTemplate.getForEntity(
                    getBaseUrl() + "/" + id,
                    CartItem.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public CartItem create(CartItem cartItem) {
        try {
            HttpEntity<CartItem> request = new HttpEntity<>(cartItem);
            ResponseEntity<CartItem> response = restTemplate.postForEntity(
                    getBaseUrl(),
                    request,
                    CartItem.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public CartItem update(Long id, CartItem cartItem) {
        try {
            cartItem.setId(id);
            HttpEntity<CartItem> request = new HttpEntity<>(cartItem);
            ResponseEntity<CartItem> response = restTemplate.exchange(
                    getBaseUrl() + "/" + id,
                    HttpMethod.PUT,
                    request,
                    CartItem.class
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

    public boolean clearCart(Long userId) {
        try {
            restTemplate.delete(getBaseUrl() + "/user/" + userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

