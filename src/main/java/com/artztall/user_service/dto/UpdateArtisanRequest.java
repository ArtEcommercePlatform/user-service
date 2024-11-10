package com.artztall.user_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateArtisanRequest {
    private String name;
    private String phoneNumber;
    private String profilePictureUrl;
    private String bio;
    private List<String> artworkCategories;
}