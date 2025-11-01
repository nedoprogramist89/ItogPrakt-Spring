package com.example.springmodels.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(clientHttpRequestFactory());
        
        // Убеждаемся, что JSON конвертер присутствует и настроен правильно
        // Spring Boot уже включает его по умолчанию, но проверим настройки
        MappingJackson2HttpMessageConverter jsonConverter = null;
        for (org.springframework.http.converter.HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                jsonConverter = (MappingJackson2HttpMessageConverter) converter;
                break;
            }
        }
        
        if (jsonConverter != null) {
            // Добавляем поддержку JSON
            jsonConverter.setSupportedMediaTypes(Arrays.asList(
                    MediaType.APPLICATION_JSON
            ));
        } else {
            // Если конвертер отсутствует, добавляем его
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            converter.setSupportedMediaTypes(Arrays.asList(
                    MediaType.APPLICATION_JSON
            ));
            restTemplate.getMessageConverters().add(converter);
        }
        
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return factory;
    }
}

