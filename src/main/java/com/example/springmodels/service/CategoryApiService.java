package com.example.springmodels.service;

import com.example.springmodels.config.ApiProperties;
import com.example.springmodels.models.Category;
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
public class CategoryApiService {

    private final RestTemplate restTemplate;
    private final ApiProperties apiProperties;

    public CategoryApiService(RestTemplate restTemplate, ApiProperties apiProperties) {
        this.restTemplate = restTemplate;
        this.apiProperties = apiProperties;
    }

    private String getBaseUrl() {
        return apiProperties.getBaseUrl() + "/categories";
    }

    public List<Category> findAll() {
        try {
            ResponseEntity<List<Category>> response = restTemplate.exchange(
                    getBaseUrl(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Category>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Category findById(Long id) {
        try {
            ResponseEntity<Category> response = restTemplate.getForEntity(
                    getBaseUrl() + "/" + id,
                    Category.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Category create(Category category) {
        try {
            HttpEntity<Category> request = new HttpEntity<>(category);
            ResponseEntity<Category> response = restTemplate.postForEntity(
                    getBaseUrl(),
                    request,
                    Category.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public Category update(Long id, Category category) {
        try {
            category.setId(id);
            HttpEntity<Category> request = new HttpEntity<>(category);
            ResponseEntity<Category> response = restTemplate.exchange(
                    getBaseUrl() + "/" + id,
                    HttpMethod.PUT,
                    request,
                    Category.class
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

