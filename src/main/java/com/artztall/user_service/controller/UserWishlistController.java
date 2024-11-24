package com.artztall.user_service.controller;

import com.artztall.user_service.dto.BuyerDTO;
import com.artztall.user_service.model.WishListItem;
import com.artztall.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Wishlist Management", description = "APIs for managing user wishlist operations")
public class UserWishlistController {

    private final UserService userService;

    @Operation(
            summary = "Add item to user's wishlist",
            description = "Adds a new item to the specified user's wishlist. If the item already exists, it won't be added again."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Item successfully added to wishlist",
                    content = @Content(schema = @Schema(implementation = BuyerDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content
            )
    })
    @PostMapping("/{userId}/wishlist")
    public ResponseEntity<BuyerDTO> addToWishlist(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable String userId,
            @Parameter(description = "Wishlist item details", required = true)
            @RequestBody WishListItem wishListItem) {
        return ResponseEntity.ok(userService.addItemToWishlist(userId, wishListItem));
    }

    @Operation(
            summary = "Remove item from user's wishlist",
            description = "Removes an item from the specified user's wishlist based on the product ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Item successfully removed from wishlist",
                    content = @Content(schema = @Schema(implementation = BuyerDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @DeleteMapping("/{userId}/wishlist/{productId}")
    public ResponseEntity<BuyerDTO> removeFromWishlist(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable String userId,
            @Parameter(description = "ID of the product to remove", required = true)
            @PathVariable String productId) {
        return ResponseEntity.ok(userService.removeItemFromWishlist(userId, productId));
    }

    @Operation(
            summary = "Get user's wishlist",
            description = "Retrieves all items in the specified user's wishlist"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Wishlist retrieved successfully",
                    content = @Content(schema = @Schema(implementation = WishListItem.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @GetMapping("/{userId}/wishlist")
    public ResponseEntity<List<WishListItem>> getWishlist(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable String userId) {
        return ResponseEntity.ok(userService.getWishlist(userId));
    }
}