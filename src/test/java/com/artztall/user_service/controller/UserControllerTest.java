package com.artztall.user_service.controller;

import com.artztall.user_service.dto.*;
import com.artztall.user_service.security.JwtAuthenticationFilter;
import com.artztall.user_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthenticationFilter authenticationFilter;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker();
    }

    @Nested
    class ArtisanTests {
        @Test
        @DisplayName("Get All Artisans")
        void testGetAllArtisans() throws Exception {
            // Create mock pageable artisan list
            List<ArtisanDTO> artisanList = IntStream.range(0, 5)
                    .mapToObj(i -> createMockArtisanDTO())
                    .collect(Collectors.toList());

            Page<ArtisanDTO> mockPage = new PageImpl<>(artisanList, PageRequest.of(0, 5), artisanList.size());

            // Mock service method
            when(userService.getAllArtisans(any(Pageable.class)))
                    .thenReturn(mockPage);

            // Perform request and validate
            mockMvc.perform(get("/api/users/artisans")
                            .param("page", "0")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(5));
        }

        @Test
        @DisplayName("Get Artisan by ID")
        void testGetArtisanById() throws Exception {
            // Create mock artisan
            ArtisanDTO mockArtisan = createMockArtisanDTO();
            String artisanId = faker.random().hex(10);

            // Mock service method
            when(userService.getArtisanById(artisanId))
                    .thenReturn(mockArtisan);

            // Perform request and validate
            mockMvc.perform(get("/api/users/artisans/{id}", artisanId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(mockArtisan.getId()))
                    .andExpect(jsonPath("$.name").value(mockArtisan.getName()));
        }

        @Test
        @DisplayName("Update Artisan Profile")
        void testUpdateArtisan() throws Exception {
            // Create mock update request and response
            String artisanId = faker.random().hex(10);
            UpdateArtisanRequest updateRequest = createMockUpdateArtisanRequest();
            ArtisanDTO updatedArtisan = createMockArtisanDTO();
            updatedArtisan.setBio(updateRequest.getBio());

            // Mock service method
            when(userService.updateArtisan(any(String.class), any(UpdateArtisanRequest.class)))
                    .thenReturn(updatedArtisan);

            // Perform request and validate
            mockMvc.perform(put("/api/users/artisans/{id}", artisanId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.bio").value(updatedArtisan.getBio()));
        }
    }

    @Nested
    class BuyerTests {
        @Test
        @DisplayName("Get All Buyers")
        void testGetAllBuyers() throws Exception {
            // Create mock pageable buyer list
            List<BuyerDTO> buyerList = IntStream.range(0, 5)
                    .mapToObj(i -> createMockBuyerDTO())
                    .collect(Collectors.toList());

            Page<BuyerDTO> mockPage = new PageImpl<>(buyerList, PageRequest.of(0, 5), buyerList.size());

            // Mock service method
            when(userService.getAllBuyers(any(Pageable.class)))
                    .thenReturn(mockPage);

            // Perform request and validate
            mockMvc.perform(get("/api/users/buyers")
                            .param("page", "0")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(5));
        }

        @Test
        @DisplayName("Get Buyer by ID")
        void testGetBuyerById() throws Exception {
            // Create mock buyer
            BuyerDTO mockBuyer = createMockBuyerDTO();
            String buyerId = faker.random().hex(10);

            // Mock service method
            when(userService.getBuyerById(buyerId))
                    .thenReturn(mockBuyer);

            // Perform request and validate
            mockMvc.perform(get("/api/users/buyers/{id}", buyerId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(mockBuyer.getId()))
                    .andExpect(jsonPath("$.name").value(mockBuyer.getName()));
        }

        @Test
        @DisplayName("Update Buyer Profile")
        void testUpdateBuyer() throws Exception {
            // Create mock update request and response
            String buyerId = faker.random().hex(10);
            UpdateBuyerRequest updateRequest = createMockUpdateBuyerRequest();
            BuyerDTO updatedBuyer = createMockBuyerDTO();
            updatedBuyer.setName(updateRequest.getName());

            // Mock service method
            when(userService.updateBuyer(any(String.class), any(UpdateBuyerRequest.class)))
                    .thenReturn(updatedBuyer);

            // Perform request and validate
            mockMvc.perform(put("/api/users/buyers/{id}", buyerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(updatedBuyer.getName()));
        }
    }

    // Helper methods for creating test data
    private ArtisanDTO createMockArtisanDTO() {
        ArtisanDTO artisan = new ArtisanDTO();
        artisan.setId(faker.random().hex(10));
        artisan.setName(faker.name().fullName());
        artisan.setEmail(faker.internet().emailAddress());
        artisan.setBio(faker.lorem().sentence());
        artisan.setArtworkCategories(
                List.of(faker.commerce().department(), faker.commerce().department())
        );
        artisan.setVerified(faker.random().nextBoolean());
        return artisan;
    }

    private BuyerDTO createMockBuyerDTO() {
        BuyerDTO buyer = new BuyerDTO();
        buyer.setId(faker.random().hex(10));
        buyer.setName(faker.name().fullName());
        buyer.setEmail(faker.internet().emailAddress());
        return buyer;
    }

    private UpdateArtisanRequest createMockUpdateArtisanRequest() {
        UpdateArtisanRequest request = new UpdateArtisanRequest();
        request.setName(faker.name().fullName());
        request.setBio(faker.lorem().sentence());
        request.setArtworkCategories(
                List.of(faker.commerce().department(), faker.commerce().department())
        );
        return request;
    }

    private UpdateBuyerRequest createMockUpdateBuyerRequest() {
        UpdateBuyerRequest request = new UpdateBuyerRequest();
        request.setName(faker.name().fullName());

        return request;
    }
}