package com.artztall.user_service.service;

import com.artztall.user_service.dto.AuthResponse;
import com.artztall.user_service.dto.LoginRequest;
import com.artztall.user_service.dto.SignupRequest;
import com.artztall.user_service.exception.UserAlreadyExistsException;
import com.artztall.user_service.exception.UserNotFoundException;
import com.artztall.user_service.model.BaseUser;
import com.artztall.user_service.model.Artisan;
import com.artztall.user_service.model.Buyer;
import com.artztall.user_service.model.UserType;
import com.artztall.user_service.repository.ArtisanRepository;
import com.artztall.user_service.repository.BuyerRepository;
import com.artztall.user_service.repository.UserRepository;
import com.artztall.user_service.security.JwtTokenProvider;
import com.artztall.user_service.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ArtisanRepository artisanRepository;
    private final BuyerRepository buyerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        // Check if user exists in any repository
        if (userRepository.existsByEmail(request.getEmail()) ||
                artisanRepository.existsByEmail(request.getEmail()) ||
                buyerRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already taken: " + request.getEmail());
        }

        BaseUser user;
        // Create user based on type
        if (request.getUserType().equals(UserType.ARTISAN.name())) {
            user = createArtisan(request);
        } else if (request.getUserType().equals(UserType.BUYER.name())) {
            user = createBuyer(request);
        } else {
            throw new IllegalArgumentException("Invalid user type: " + request.getUserType());
        }

        // Set common user properties
        setCommonUserProperties(user, request);

        // Save user based on type
        if (user instanceof Artisan) {
            user = artisanRepository.save((Artisan) user);
        } else {
            user = buyerRepository.save((Buyer) user);
            System.out.println(user);
        }

        // Generate JWT token
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String token = tokenProvider.generateToken(userDetails);

        return createAuthResponse(user, token);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Find user in appropriate repository based on type
            BaseUser user = findUserByEmail(request.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

            // Update last login date
            user.setLastLoginDate(LocalDateTime.now());
            if (user instanceof Artisan) {
                user = artisanRepository.save((Artisan) user);
            } else {
                user = buyerRepository.save((Buyer) user);
            }

            // Generate new token
            String token = tokenProvider.generateToken(userDetails);

            return createAuthResponse(user, token);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    private Artisan createArtisan(SignupRequest request) {
        Artisan artisan = new Artisan();
        artisan.setBio(request.getBio());
        artisan.setArtworkCategories(request.getArtworkCategories());
        artisan.setAverageRating(0.0);
        artisan.setTotalSales(0);
        artisan.setVerified(false);
        return artisan;
    }

    private Buyer createBuyer(SignupRequest request) {
        Buyer buyer = new Buyer();
        buyer.setAddress(request.getAddress());
        buyer.setFavoriteArtisans(new ArrayList<>());
        buyer.setRecentlyViewedProducts(new ArrayList<>());
        return buyer;
    }

    private void setCommonUserProperties(BaseUser user, SignupRequest request) {
        user.setEmail(request.getEmail());
        user.setProfilePictureUrl(request.getProfImg());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setJoinDate(LocalDateTime.now());
        user.setUserType(UserType.valueOf(request.getUserType()));
        user.setActive(true);
    }

    private Optional<BaseUser> findUserByEmail(String email) {
        Optional<Artisan> artisan = artisanRepository.findByEmail(email);
        if (artisan.isPresent()) {
            return Optional.of(artisan.get());
        }

        Optional<Buyer> buyer = buyerRepository.findByEmail(email);
        return buyer.map(value -> value);
    }

    private AuthResponse createAuthResponse(BaseUser user, String token) {
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setUserType(user.getUserType().name());
        response.setProfImg(user.getProfilePictureUrl());

        // Add type-specific information
        if (user instanceof Artisan artisan) {
            response.setBio(artisan.getBio());
            response.setArtworkCategories(artisan.getArtworkCategories());
            response.setVerified(artisan.isVerified());
        } else if (user instanceof Buyer buyer) {
            response.setAddress(buyer.getAddress());
        }

        return response;
    }
}