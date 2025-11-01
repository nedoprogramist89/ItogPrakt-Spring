package com.example.springmodels.service;

import com.example.springmodels.config.ApiProperties;
import com.example.springmodels.models.Product;
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
public class ProductApiService {

    private final RestTemplate restTemplate;
    private final ApiProperties apiProperties;

    public ProductApiService(RestTemplate restTemplate, ApiProperties apiProperties) {
        this.restTemplate = restTemplate;
        this.apiProperties = apiProperties;
    }

    private String getBaseUrl() {
        return apiProperties.getBaseUrl() + "/products";
    }

    public List<Product> findAll() {
        try {
            ResponseEntity<List<Product>> response = restTemplate.exchange(
                    getBaseUrl(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Product>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Product findById(Long id) {
        try {
            ResponseEntity<Product> response = restTemplate.getForEntity(
                    getBaseUrl() + "/" + id,
                    Product.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Product> findByCategoryId(Long categoryId) {
        try {
            ResponseEntity<List<Product>> response = restTemplate.exchange(
                    getBaseUrl() + "/category/" + categoryId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Product>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Product> findByManufacturerId(Long manufacturerId) {
        try {
            ResponseEntity<List<Product>> response = restTemplate.exchange(
                    getBaseUrl() + "/manufacturer/" + manufacturerId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Product>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Product create(Product product) {
        try {
            HttpEntity<Product> request = new HttpEntity<>(product);
            ResponseEntity<Product> response = restTemplate.postForEntity(
                    getBaseUrl(),
                    request,
                    Product.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public Product update(Long id, Product product) {
        try {
            product.setId(id);
            HttpEntity<Product> request = new HttpEntity<>(product);
            ResponseEntity<Product> response = restTemplate.exchange(
                    getBaseUrl() + "/" + id,
                    HttpMethod.PUT,
                    request,
                    Product.class
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

