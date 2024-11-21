package com.artztall.user_service.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "buyers")
public class Buyer extends BaseUser {
    private Address address;
    private List<String> favoriteArtisans;
    private List<String> recentlyViewedProducts;
}
