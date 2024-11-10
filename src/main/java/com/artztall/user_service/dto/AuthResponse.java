package com.artztall.user_service.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String id;
    private String email;
    private String name;
    private String userType;
}