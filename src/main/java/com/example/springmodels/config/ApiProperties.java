package com.example.springmodels.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiProperties {

    @Value("${api.base-url}")
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }
}

