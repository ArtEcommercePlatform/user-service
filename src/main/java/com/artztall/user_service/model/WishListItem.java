package com.artztall.user_service.model;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WishListItem {
    private String productId;
    private LocalDateTime addedOn;
    private String note;
}
