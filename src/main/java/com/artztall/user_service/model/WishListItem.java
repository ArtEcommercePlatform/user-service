package com.artztall.user_service.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishListItem {
    private String productId;
    private LocalDateTime addedOn;
    private String note;
}
