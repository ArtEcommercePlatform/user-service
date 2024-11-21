package com.artztall.user_service.dto;

import com.artztall.user_service.model.Address;
import lombok.Data;

import java.util.List;

@Data
public class AuthResponse {
    private String token;
    private String id;
    private String email;
    private String name;
    private String userType;

    // Artisan-specific fields
    private String bio;
    private List<String> artworkCategories;
    private boolean isVerified;

    // Buyer-specific fields
    private Address address;
}