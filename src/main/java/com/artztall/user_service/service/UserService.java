package com.artztall.user_service.service;


import com.artztall.user_service.dto.ArtisanDTO;
import com.artztall.user_service.dto.BuyerDTO;
import com.artztall.user_service.dto.UpdateArtisanRequest;
import com.artztall.user_service.dto.UpdateBuyerRequest;
import com.artztall.user_service.model.Artisan;
import com.artztall.user_service.model.Buyer;
import com.artztall.user_service.model.WishListItem;
import com.artztall.user_service.repository.BuyerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.artztall.user_service.repository.ArtisanRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ArtisanRepository artisanRepository;
    private final BuyerRepository buyerRepository;

    // Artisan methods
    public Page<ArtisanDTO> getAllArtisans(Pageable pageable) {
        // Fetch all artisans from the repository with pagination
        Page<Artisan> artisansPage = artisanRepository.findAll(pageable);

        // Convert each Artisan object to ArtisanDTO using the map() method
        return artisansPage.map(this::convertToArtisanDTO);
    }

    public ArtisanDTO getArtisanById(String id) {
        Artisan artisan = artisanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artisan not found"));
        return convertToArtisanDTO(artisan);
    }

    public ArtisanDTO updateArtisan(String id, UpdateArtisanRequest request) {
        Artisan artisan = artisanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artisan not found"));

        if (request.getName() != null) artisan.setName(request.getName());
        if (request.getPhoneNumber() != null) artisan.setPhoneNumber(request.getPhoneNumber());
        if (request.getProfilePictureUrl() != null) artisan.setProfilePictureUrl(request.getProfilePictureUrl());
        if (request.getBio() != null) artisan.setBio(request.getBio());
        if (request.getArtworkCategories() != null) artisan.setArtworkCategories(request.getArtworkCategories());

        return convertToArtisanDTO(artisanRepository.save(artisan));
    }

    // Buyer methods
    public Page<BuyerDTO> getAllBuyers(Pageable pageable) {
        return buyerRepository.findAll(pageable)
                .map(this::convertToBuyerDTO);
    }

    public BuyerDTO getBuyerById(String id) {
        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        return convertToBuyerDTO(buyer);
    }

    public BuyerDTO updateBuyer(String id, UpdateBuyerRequest request) {
        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        if (request.getName() != null) buyer.setName(request.getName());
        if (request.getPhoneNumber() != null) buyer.setPhoneNumber(request.getPhoneNumber());
        if (request.getProfilePictureUrl() != null) buyer.setProfilePictureUrl(request.getProfilePictureUrl());
        if (request.getAddresses() != null) buyer.setAddress(request.getAddresses());


        return convertToBuyerDTO(buyerRepository.save(buyer));
    }

    public BuyerDTO addItemToWishlist(String buyerId, WishListItem wishListItem) {
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        // Set the current timestamp if not already set
        if (wishListItem.getAddedOn() == null) {
            wishListItem.setAddedOn(LocalDateTime.now());
        }

        // Initialize wishlist if null
        if (buyer.getWhishList() == null) {
            buyer.setWhishList(new ArrayList<>());
        }

        // Check if product already exists in wishlist
        boolean productExists = buyer.getWhishList().stream()
                .anyMatch(item -> item.getProductId().equals(wishListItem.getProductId()));

        if (!productExists) {
            buyer.getWhishList().add(wishListItem);
            Buyer savedBuyer = buyerRepository.save(buyer);
            return convertToBuyerDTO(savedBuyer);
        }

        return convertToBuyerDTO(buyer);
    }

    public BuyerDTO removeItemFromWishlist(String buyerId, String productId) {
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        if (buyer.getWhishList() != null) {
            buyer.getWhishList().removeIf(item -> item.getProductId().equals(productId));
            Buyer savedBuyer = buyerRepository.save(buyer);
            return convertToBuyerDTO(savedBuyer);
        }

        return convertToBuyerDTO(buyer);
    }

    public List<WishListItem> getWishlist(String buyerId) {
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        return buyer.getWhishList() != null ? buyer.getWhishList() : new ArrayList<>();
    }
    // Helper methods
    private ArtisanDTO convertToArtisanDTO(Artisan artisan) {
        ArtisanDTO dto = new ArtisanDTO();
        dto.setId(artisan.getId());
        dto.setEmail(artisan.getEmail());
        dto.setName(artisan.getName());
        dto.setPhoneNumber(artisan.getPhoneNumber());
        dto.setProfilePictureUrl(artisan.getProfilePictureUrl());
        dto.setBio(artisan.getBio());
        dto.setArtworkCategories(artisan.getArtworkCategories());
        dto.setAverageRating(artisan.getAverageRating());
        dto.setTotalSales(artisan.getTotalSales());
        dto.setVerified(artisan.isVerified());
        return dto;
    }

    private BuyerDTO convertToBuyerDTO(Buyer buyer) {
        BuyerDTO dto = new BuyerDTO();
        dto.setId(buyer.getId());
        dto.setEmail(buyer.getEmail());
        dto.setName(buyer.getName());
        dto.setPhoneNumber(buyer.getPhoneNumber());
        dto.setProfilePictureUrl(buyer.getProfilePictureUrl());
        dto.setAddresses(buyer.getAddress());
        dto.setFavoriteArtisans(buyer.getFavoriteArtisans());
        dto.setRecentlyViewedProducts(buyer.getRecentlyViewedProducts());
        dto.setWishlist(buyer.getWhishList());
        return dto;
    }
}
