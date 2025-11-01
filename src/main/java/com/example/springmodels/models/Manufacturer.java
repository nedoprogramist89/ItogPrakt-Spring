package com.example.springmodels.models;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "manufacturers")
public class Manufacturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название производителя не может быть пустым")
    @Size(max = 100, message = "Название производителя не должно превышать 100 символов")
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;

    @Size(max = 100, message = "Страна не должна превышать 100 символов")
    private String country;

    @Email(message = "Email должен быть корректным")
    @Size(max = 100, message = "Email не должен превышать 100 символов")
    private String email;

    @Size(max = 20, message = "Телефон не должен превышать 20 символов")
    private String phone;

    @OneToMany(mappedBy = "manufacturer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    public Manufacturer() {}

    public Manufacturer(String name, String description, String country, String email, String phone) {
        this.name = name;
        this.description = description;
        this.country = country;
        this.email = email;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}

