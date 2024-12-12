package com.artztall.user_service.service;

import com.artztall.user_service.dto.ArtisanDTO;
import com.artztall.user_service.dto.BuyerDTO;
import com.artztall.user_service.dto.UpdateArtisanRequest;
import com.artztall.user_service.dto.UpdateBuyerRequest;
import com.artztall.user_service.model.Artisan;
import com.artztall.user_service.model.Buyer;
import com.artztall.user_service.model.WishListItem;
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

import java.util.ArrayList;
import java.util.Arrays;
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

    @Mock
    private Pageable pageable;

    private Artisan artisan;
    private Buyer buyer;
    private WishListItem wishListItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        artisan = new Artisan();
        artisan.setId("artisan-id");
        artisan.setName("Test Artisan");
        artisan.setEmail("artisan@example.com");

        buyer = new Buyer();
        buyer.setId("buyer-id");
        buyer.setName("Test Buyer");
        buyer.setEmail("buyer@example.com");

        wishListItem = new WishListItem();
        wishListItem.setProductId("product-id");
    }

    // Test Artisan Methods

    @Test
    void testGetAllArtisans() {
        Page<Artisan> artisanPage = new PageImpl<>(Arrays.asList(artisan));
        when(artisanRepository.findAll(pageable)).thenReturn(artisanPage);

        Page<ArtisanDTO> result = userService.getAllArtisans(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Artisan", result.getContent().get(0).getName());
        verify(artisanRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetArtisanById() {
        when(artisanRepository.findById("artisan-id")).thenReturn(Optional.of(artisan));

        ArtisanDTO result = userService.getArtisanById("artisan-id");

        assertNotNull(result);
        assertEquals("Test Artisan", result.getName());
        verify(artisanRepository, times(1)).findById("artisan-id");
    }

    @Test
    void testUpdateArtisan() {
        UpdateArtisanRequest request = new UpdateArtisanRequest();
        request.setName("Updated Artisan");

        when(artisanRepository.findById("artisan-id")).thenReturn(Optional.of(artisan));
        when(artisanRepository.save(artisan)).thenReturn(artisan);

        ArtisanDTO result = userService.updateArtisan("artisan-id", request);

        assertNotNull(result);
        assertEquals("Updated Artisan", result.getName());
        verify(artisanRepository, times(1)).findById("artisan-id");
        verify(artisanRepository, times(1)).save(artisan);
    }

    // Test Buyer Methods

    @Test
    void testGetAllBuyers() {
        Page<Buyer> buyerPage = new PageImpl<>(Arrays.asList(buyer));
        when(buyerRepository.findAll(pageable)).thenReturn(buyerPage);

        Page<BuyerDTO> result = userService.getAllBuyers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Buyer", result.getContent().get(0).getName());
        verify(buyerRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetBuyerById() {
        when(buyerRepository.findById("buyer-id")).thenReturn(Optional.of(buyer));

        BuyerDTO result = userService.getBuyerById("buyer-id");

        assertNotNull(result);
        assertEquals("Test Buyer", result.getName());
        verify(buyerRepository, times(1)).findById("buyer-id");
    }

    @Test
    void testUpdateBuyer() {
        UpdateBuyerRequest request = new UpdateBuyerRequest();
        request.setName("Updated Buyer");

        when(buyerRepository.findById("buyer-id")).thenReturn(Optional.of(buyer));
        when(buyerRepository.save(buyer)).thenReturn(buyer);

        BuyerDTO result = userService.updateBuyer("buyer-id", request);

        assertNotNull(result);
        assertEquals("Updated Buyer", result.getName());
        verify(buyerRepository, times(1)).findById("buyer-id");
        verify(buyerRepository, times(1)).save(buyer);
    }

    @Test
    void testAddItemToWishlist() {
        // Ensure the wishlist is initialized
        buyer.setWhishList(new ArrayList<>());

        when(buyerRepository.findById("buyer-id")).thenReturn(Optional.of(buyer));
        when(buyerRepository.save(buyer)).thenReturn(buyer);

        BuyerDTO result = userService.addItemToWishlist("buyer-id", wishListItem);

        assertNotNull(result);
        assertEquals(1, result.getWishlist().size());
        assertEquals("product-id", result.getWishlist().get(0).getProductId());
        verify(buyerRepository, times(1)).findById("buyer-id");
        verify(buyerRepository, times(1)).save(buyer);
    }


    @Test
    void testRemoveItemFromWishlist() {
        // Use a mutable list instead of Arrays.asList
        buyer.setWhishList(new ArrayList<>(List.of(wishListItem)));

        when(buyerRepository.findById("buyer-id")).thenReturn(Optional.of(buyer));
        when(buyerRepository.save(any(Buyer.class))).thenReturn(buyer);

        BuyerDTO result = userService.removeItemFromWishlist("buyer-id", "product-id");

        assertNotNull(result);
        assertFalse(result.getWishlist().stream()
                .anyMatch(item -> "product-id".equals(item.getProductId())));
        verify(buyerRepository, times(1)).findById("buyer-id");
        verify(buyerRepository, times(1)).save(buyer);
    }


    @Test
    void testGetWishlist() {
        buyer.setWhishList(Arrays.asList(wishListItem));

        when(buyerRepository.findById("buyer-id")).thenReturn(Optional.of(buyer));

        List<WishListItem> result = userService.getWishlist("buyer-id");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("product-id", result.get(0).getProductId());
        verify(buyerRepository, times(1)).findById("buyer-id");
    }
}
