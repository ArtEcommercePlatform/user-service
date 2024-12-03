package com.artztall.user_service.service;

import com.artztall.user_service.dto.ArtisanDTO;
import com.artztall.user_service.dto.BuyerDTO;
import com.artztall.user_service.dto.UpdateArtisanRequest;
import com.artztall.user_service.dto.UpdateBuyerRequest;
import com.artztall.user_service.model.*;
import com.artztall.user_service.repository.ArtisanRepository;
import com.artztall.user_service.repository.BuyerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private ArtisanRepository artisanRepository;

    @Mock
    private BuyerRepository buyerRepository;

    private Artisan artisan;
    private Buyer buyer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize Artisan
        artisan = new Artisan();
        artisan.setId("artisan1");
        artisan.setName("John Doe");
        artisan.setEmail("john.doe@example.com");
        artisan.setPhoneNumber("123456789");
        artisan.setBio("An experienced artisan");
        artisan.setArtworkCategories(List.of("Painting", "Sculpture"));
        artisan.setAverageRating(4.5);
        artisan.setTotalSales(120);
        artisan.setVerified(true);

        // Initialize Buyer
        buyer = new Buyer();
        buyer.setId("buyer1");
        buyer.setName("Jane Doe");
        buyer.setEmail("jane.doe@example.com");
        buyer.setPhoneNumber("987654321");
        Address address = new Address();
        address.setId("addr1");
        address.setStreet("123 Main St");
        address.setCity("City");
        address.setState("State");
        address.setCountry("Country");
        address.setPostalCode("12345");
        address.setDefault(true);
        buyer.setAddress(address);
        buyer.setFavoriteArtisans(List.of("artisan1"));
        buyer.setRecentlyViewedProducts(List.of("product1", "product2"));
        buyer.setWhishList(new ArrayList<>());
    }

    @Test
    void getAllArtisans() {
        Page<Artisan> artisanPage = new PageImpl<>(List.of(artisan));
        when(artisanRepository.findAll(any(Pageable.class))).thenReturn(artisanPage);

        Page<ArtisanDTO> result = userService.getAllArtisans(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("John Doe", result.getContent().get(0).getName());
    }

    @Test
    void getArtisanById() {
        when(artisanRepository.findById("artisan1")).thenReturn(Optional.of(artisan));

        ArtisanDTO result = userService.getArtisanById("artisan1");

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals(4.5, result.getAverageRating());
    }

    @Test
    void updateArtisan() {
        UpdateArtisanRequest request = new UpdateArtisanRequest();
        request.setName("John Updated");
        request.setBio("Updated bio");

        when(artisanRepository.findById("artisan1")).thenReturn(Optional.of(artisan));
        when(artisanRepository.save(any(Artisan.class))).thenReturn(artisan);

        ArtisanDTO result = userService.updateArtisan("artisan1", request);

        assertNotNull(result);
        assertEquals("John Updated", result.getName());
        assertEquals("Updated bio", result.getBio());
    }

    @Test
    void getAllBuyers() {
        Page<Buyer> buyerPage = new PageImpl<>(List.of(buyer));
        when(buyerRepository.findAll(any(Pageable.class))).thenReturn(buyerPage);

        Page<BuyerDTO> result = userService.getAllBuyers(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Jane Doe", result.getContent().get(0).getName());
    }

    @Test
    void getBuyerById() {
        when(buyerRepository.findById("buyer1")).thenReturn(Optional.of(buyer));

        BuyerDTO result = userService.getBuyerById("buyer1");

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        assertEquals("123 Main St", result.getAddresses().getStreet());
    }

    @Test
    void updateBuyer() {
        UpdateBuyerRequest request = new UpdateBuyerRequest();
        request.setName("Jane Updated");
        Address newAddress = new Address();
        newAddress.setId("addr2");
        newAddress.setStreet("456 New St");
        newAddress.setCity("New City");
        newAddress.setState("New State");
        newAddress.setCountry("New Country");
        newAddress.setPostalCode("67890");
        newAddress.setDefault(true);
        request.setAddresses(newAddress);

        when(buyerRepository.findById("buyer1")).thenReturn(Optional.of(buyer));
        when(buyerRepository.save(any(Buyer.class))).thenReturn(buyer);

        BuyerDTO result = userService.updateBuyer("buyer1", request);

        assertNotNull(result);
        assertEquals("Jane Updated", result.getName());
        assertEquals("456 New St", result.getAddresses().getStreet());
    }


    @Test
    void addItemToWishlist() {
        WishListItem item = new WishListItem();
        item.setProductId("product1");
        item.setAddedOn(LocalDateTime.now());

        when(buyerRepository.findById("buyer1")).thenReturn(Optional.of(buyer));
        when(buyerRepository.save(any(Buyer.class))).thenReturn(buyer);

        BuyerDTO result = userService.addItemToWishlist("buyer1", item);

        assertNotNull(result);
        assertEquals(1, result.getWishlist().size());
    }


    @Test
    void removeItemFromWishlist() {
        WishListItem item = new WishListItem();
        item.setProductId("product1");
        item.setAddedOn(LocalDateTime.now());

        buyer.getWhishList().add(item);

        when(buyerRepository.findById("buyer1")).thenReturn(Optional.of(buyer));
        when(buyerRepository.save(any(Buyer.class))).thenReturn(buyer);

        BuyerDTO result = userService.removeItemFromWishlist("buyer1", "product1");

        assertNotNull(result);
        assertEquals(0, result.getWishlist().size());
    }
}
