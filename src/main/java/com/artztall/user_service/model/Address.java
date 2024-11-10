package com.artztall.user_service.model;

import lombok.Data;

@Data
public class Address {
    private String id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private boolean isDefault;
}
