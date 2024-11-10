package com.artztall.user_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class ArtisanDTO {
    private String id;
    private String email;
    private String name;
    private String phoneNumber;
    private String profilePictureUrl;
    private String bio;
    private List<String> artworkCategories;
    private double averageRating;
    private int totalSales;
    private boolean isVerified;
}