package com.artztall.user_service.dto;

import com.artztall.user_service.model.Address;
import lombok.Data;

import java.util.List;

@Data
public class BuyerDTO {
    private String id;
    private String email;
    private String name;
    private String phoneNumber;
    private String profilePictureUrl;
    private Address addresses;
    private String defaultAddressId;
    private List<String> favoriteArtisans;
    private List<String> recentlyViewedProducts;
}