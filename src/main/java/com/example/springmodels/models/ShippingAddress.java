package com.example.springmodels.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "shipping_addresses")
public class ShippingAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Город не может быть пустым")
    @Size(max = 100, message = "Город не должен превышать 100 символов")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "Улица не может быть пустой")
    @Size(max = 200, message = "Улица не должна превышать 200 символов")
    @Column(nullable = false)
    private String street;

    @NotBlank(message = "Дом не может быть пустым")
    @Size(max = 20, message = "Дом не должен превышать 20 символов")
    @Column(nullable = false)
    private String house;

    @Size(max = 20, message = "Квартира не должна превышать 20 символов")
    private String apartment;

    @Size(max = 10, message = "Почтовый индекс не должен превышать 10 символов")
    private String postalCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private ModelUser user;

    @OneToMany(mappedBy = "shippingAddress", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    public ShippingAddress() {}

    public ShippingAddress(String city, String street, String house, String apartment, String postalCode, ModelUser user) {
        this.city = city;
        this.street = street;
        this.house = house;
        this.apartment = apartment;
        this.postalCode = postalCode;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public ModelUser getUser() {
        return user;
    }

    public void setUser(ModelUser user) {
        this.user = user;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}

