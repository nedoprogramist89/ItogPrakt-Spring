package com.example.springmodels.service;

import com.example.springmodels.config.ApiProperties;
import com.example.springmodels.models.Manufacturer;
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
public class ManufacturerApiService {

    private final RestTemplate restTemplate;
    private final ApiProperties apiProperties;

    public ManufacturerApiService(RestTemplate restTemplate, ApiProperties apiProperties) {
        this.restTemplate = restTemplate;
        this.apiProperties = apiProperties;
    }

    private String getBaseUrl() {
        return apiProperties.getBaseUrl() + "/manufacturers";
    }

    public List<Manufacturer> findAll() {
        try {
            ResponseEntity<List<Manufacturer>> response = restTemplate.exchange(
                    getBaseUrl(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Manufacturer>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Manufacturer findById(Long id) {
        try {
            ResponseEntity<Manufacturer> response = restTemplate.getForEntity(
                    getBaseUrl() + "/" + id,
                    Manufacturer.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Manufacturer create(Manufacturer manufacturer) {
        try {
            HttpEntity<Manufacturer> request = new HttpEntity<>(manufacturer);
            ResponseEntity<Manufacturer> response = restTemplate.postForEntity(
                    getBaseUrl(),
                    request,
                    Manufacturer.class
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public Manufacturer update(Long id, Manufacturer manufacturer) {
        try {
            manufacturer.setId(id);
            HttpEntity<Manufacturer> request = new HttpEntity<>(manufacturer);
            ResponseEntity<Manufacturer> response = restTemplate.exchange(
                    getBaseUrl() + "/" + id,
                    HttpMethod.PUT,
                    request,
                    Manufacturer.class
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

