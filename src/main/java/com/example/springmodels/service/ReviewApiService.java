package com.example.springmodels.service;

import com.example.springmodels.config.ApiProperties;
import com.example.springmodels.models.Review;
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
public class ReviewApiService {

    private final RestTemplate restTemplate;
    private final ApiProperties apiProperties;

    public ReviewApiService(RestTemplate restTemplate, ApiProperties apiProperties) {
        this.restTemplate = restTemplate;
        this.apiProperties = apiProperties;
    }

    private String getBaseUrl() {
        return apiProperties.getBaseUrl() + "/reviews";
    }

    public List<Review> findAll() {
        try {
            ResponseEntity<List<Review>> response = restTemplate.exchange(
                    getBaseUrl(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Review>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Review> findByProductId(Long productId) {
        try {
            ResponseEntity<List<Review>> response = restTemplate.exchange(
                    getBaseUrl() + "/product/" + productId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Review>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Review> findByUserId(Long userId) {
        try {
            ResponseEntity<List<Review>> response = restTemplate.exchange(
                    getBaseUrl() + "/user/" + userId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Review>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Review findById(Long id) {
        try {
            ResponseEntity<Review> response = restTemplate.getForEntity(
                    getBaseUrl() + "/" + id,
                    Review.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Review create(Review review) {
        try {
            Long reviewId = review.getId();
            com.example.springmodels.models.ModelUser originalUser = review.getUser();
            com.example.springmodels.models.Product originalProduct = review.getProduct();
            
            review.setId(null);
            review.setReviewDate(null);
            
            com.example.springmodels.models.ModelUser userForApi = null;
            if (originalUser != null && originalUser.getIdUser() != null) {
                userForApi = new com.example.springmodels.models.ModelUser();
                userForApi.setIdUser(originalUser.getIdUser());
                userForApi.setUsername(originalUser.getUsername() != null ? originalUser.getUsername() : "");
                userForApi.setEmail(originalUser.getEmail() != null ? originalUser.getEmail() : "");
                userForApi.setFullName(originalUser.getFullName() != null ? originalUser.getFullName() : "");
            }
            
            com.example.springmodels.models.Product productForApi = null;
            if (originalProduct != null && originalProduct.getId() != null) {
                productForApi = new com.example.springmodels.models.Product();
                productForApi.setId(originalProduct.getId());
                productForApi.setName(originalProduct.getName() != null ? originalProduct.getName() : "");
                productForApi.setPrice(originalProduct.getPrice() != null ? originalProduct.getPrice() : java.math.BigDecimal.ZERO);
                productForApi.setQuantity(originalProduct.getQuantity() != null ? originalProduct.getQuantity() : 0);
                if (originalProduct.getCategory() != null) {
                    productForApi.setCategory(originalProduct.getCategory());
                }
                if (originalProduct.getManufacturer() != null) {
                    productForApi.setManufacturer(originalProduct.getManufacturer());
                }
            }
            
            review.setUser(userForApi);
            review.setProduct(productForApi);
            
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            
            HttpEntity<Review> request = new HttpEntity<>(review, headers);
            
            ResponseEntity<Review> response = restTemplate.postForEntity(
                    getBaseUrl(),
                    request,
                    Review.class
            );
            
            review.setUser(originalUser);
            review.setProduct(originalProduct);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                review.setId(reviewId);
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public Review update(Long id, Review review) {
        try {
            review.setId(id);
            HttpEntity<Review> request = new HttpEntity<>(review);
            ResponseEntity<Review> response = restTemplate.exchange(
                    getBaseUrl() + "/" + id,
                    HttpMethod.PUT,
                    request,
                    Review.class
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

