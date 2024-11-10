package com.artztall.user_service.service;


import com.artztall.user_service.dto.*;
import com.artztall.user_service.model.*;
import com.artztall.user_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ArtisanRepository artisanRepository;
    private final BuyerRepository buyerRepository;

    // Artisan methods
    public Page<ArtisanDTO> getAllArtisans(Pageable pageable) {
        return artisanRepository.findAll(pageable)
                .map(this::convertToArtisanDTO);
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
        if (request.getAddresses() != null) buyer.setAddresses(request.getAddresses());


        return convertToBuyerDTO(buyerRepository.save(buyer));
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
        dto.setAddresses(buyer.getAddresses());
        dto.setFavoriteArtisans(buyer.getFavoriteArtisans());
        dto.setRecentlyViewedProducts(buyer.getRecentlyViewedProducts());
        return dto;
    }
}
