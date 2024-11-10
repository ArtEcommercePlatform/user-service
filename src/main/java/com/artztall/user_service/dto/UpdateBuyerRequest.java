package com.artztall.user_service.dto;

import com.artztall.user_service.model.Address;
import lombok.Data;

@Data
public class UpdateBuyerRequest {
    private String name;
    private String phoneNumber;
    private String profilePictureUrl;
    private Address addresses;
    private String defaultAddressId;
}
