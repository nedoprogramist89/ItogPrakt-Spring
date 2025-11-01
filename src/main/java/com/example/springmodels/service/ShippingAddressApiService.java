package com.example.springmodels.service;

import com.example.springmodels.config.ApiProperties;
import com.example.springmodels.models.ShippingAddress;
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
public class ShippingAddressApiService {

    private final RestTemplate restTemplate;
    private final ApiProperties apiProperties;

    public ShippingAddressApiService(RestTemplate restTemplate, ApiProperties apiProperties) {
        this.restTemplate = restTemplate;
        this.apiProperties = apiProperties;
    }

    private String getBaseUrl() {
        return apiProperties.getBaseUrl() + "/addresses";
    }

    public List<ShippingAddress> findAll() {
        try {
            ResponseEntity<List<ShippingAddress>> response = restTemplate.exchange(
                    getBaseUrl(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ShippingAddress>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<ShippingAddress> findByUserId(Long userId) {
        try {
            ResponseEntity<List<ShippingAddress>> response = restTemplate.exchange(
                    getBaseUrl() + "/user/" + userId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ShippingAddress>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public ShippingAddress findById(Long id) {
        try {
            ResponseEntity<ShippingAddress> response = restTemplate.getForEntity(
                    getBaseUrl() + "/" + id,
                    ShippingAddress.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public ShippingAddress create(ShippingAddress address) {
        try {
            HttpEntity<ShippingAddress> request = new HttpEntity<>(address);
            ResponseEntity<ShippingAddress> response = restTemplate.postForEntity(
                    getBaseUrl(),
                    request,
                    ShippingAddress.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public ShippingAddress update(Long id, ShippingAddress address) {
        try {
            address.setId(id);
            HttpEntity<ShippingAddress> request = new HttpEntity<>(address);
            ResponseEntity<ShippingAddress> response = restTemplate.exchange(
                    getBaseUrl() + "/" + id,
                    HttpMethod.PUT,
                    request,
                    ShippingAddress.class
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

