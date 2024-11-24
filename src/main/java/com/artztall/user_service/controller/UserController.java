package com.artztall.user_service.controller;

import com.artztall.user_service.dto.*;
import com.artztall.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs for both Artisans and Buyers")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Get all artisans",
            description = "Retrieves a paginated list of all artisans"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved artisans",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArtisanDTO.class))
            )
    })
    @GetMapping("/artisans")
    public ResponseEntity<Page<ArtisanDTO>> getAllArtisans(
            @Parameter(description = "Pagination parameters") Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getAllArtisans(pageable));
    }

    @Operation(
            summary = "Get artisan by ID",
            description = "Retrieves an artisan's details by their ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved artisan"),
            @ApiResponse(responseCode = "404", description = "Artisan not found")
    })
    @GetMapping("/artisans/{id}")
    public ResponseEntity<ArtisanDTO> getArtisanById(
            @Parameter(description = "Artisan ID") @PathVariable String id
    ) {
        return ResponseEntity.ok(userService.getArtisanById(id));
    }

    @Operation(
            summary = "Update artisan profile",
            description = "Updates an artisan's profile information"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated artisan"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Artisan not found")
    })
    @PutMapping("/artisans/{id}")
    public ResponseEntity<ArtisanDTO> updateArtisan(
            @Parameter(description = "Artisan ID") @PathVariable String id,
            @Valid @RequestBody UpdateArtisanRequest request
    ) {
        return ResponseEntity.ok(userService.updateArtisan(id, request));
    }

    @Operation(
            summary = "Get all buyers",
            description = "Retrieves a paginated list of all buyers"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved buyers",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BuyerDTO.class))
            )
    })
    @GetMapping("/buyers")
    public ResponseEntity<Page<BuyerDTO>> getAllBuyers(
            @Parameter(description = "Pagination parameters") Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getAllBuyers(pageable));
    }

    @Operation(
            summary = "Get buyer by ID",
            description = "Retrieves a buyer's details by their ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved buyer"),
            @ApiResponse(responseCode = "404", description = "Buyer not found")
    })
    @GetMapping("/buyers/{id}")
    public ResponseEntity<BuyerDTO> getBuyerById(
            @Parameter(description = "Buyer ID") @PathVariable String id
    ) {
        return ResponseEntity.ok(userService.getBuyerById(id));
    }

    @Operation(
            summary = "Update buyer profile",
            description = "Updates a buyer's profile information"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated buyer"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Buyer not found")
    })
    @PutMapping("/buyers/{id}")
    public ResponseEntity<BuyerDTO> updateBuyer(
            @Parameter(description = "Buyer ID") @PathVariable String id,
            @Valid @RequestBody UpdateBuyerRequest request
    ) {
        return ResponseEntity.ok(userService.updateBuyer(id, request));
    }

}