package com.artztall.user_service.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "artisans")
public class Artisan extends BaseUser {
    private String bio;
    private List<String> artworkCategories;
    private double averageRating;
    private int totalSales;
    private boolean isVerified;
}