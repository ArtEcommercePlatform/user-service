package com.artztall.user_service.controller;

import com.artztall.user_service.dto.*;
import com.artztall.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Artisan endpoints
    @GetMapping("/artisans")
    public ResponseEntity<Page<ArtisanDTO>> getAllArtisans(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllArtisans(pageable));
    }

    @GetMapping("/artisans/{id}")
    public ResponseEntity<ArtisanDTO> getArtisanById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getArtisanById(id));
    }

    @PutMapping("/artisans/{id}")
    public ResponseEntity<ArtisanDTO> updateArtisan(
            @PathVariable String id,
            @RequestBody UpdateArtisanRequest request) {
        return ResponseEntity.ok(userService.updateArtisan(id, request));
    }

    // Buyer endpoints
    @GetMapping("/buyers")
    public ResponseEntity<Page<BuyerDTO>> getAllBuyers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllBuyers(pageable));
    }

    @GetMapping("/buyers/{id}")
    public ResponseEntity<BuyerDTO> getBuyerById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getBuyerById(id));
    }

    @PutMapping("/buyers/{id}")
    public ResponseEntity<BuyerDTO> updateBuyer(
            @PathVariable String id,
            @RequestBody UpdateBuyerRequest request) {
        return ResponseEntity.ok(userService.updateBuyer(id, request));
    }
}