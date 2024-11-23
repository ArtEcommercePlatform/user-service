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
        validateSignupRequest(request);

        // Check if user exists in any repository
        if (isEmailTaken(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already taken: " + request.getEmail());
        }

        BaseUser user = createUserByType(request);
        setCommonUserProperties(user, request);
        user = saveUser(user);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String token = tokenProvider.generateToken(userDetails);

        return createAuthResponse(user, token);
    }

    private boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email) ||
                artisanRepository.existsByEmail(email) ||
                buyerRepository.existsByEmail(email);
    }

    private void validateSignupRequest(SignupRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (request.getUserType() == null) {
            throw new IllegalArgumentException("User type must be specified");
        }
    }

    private BaseUser createUserByType(SignupRequest request) {
        try {
            UserType userType = UserType.valueOf(request.getUserType());
            return switch (userType) {
                case ARTISAN -> createArtisan(request);
                case BUYER -> createBuyer(request);
                default -> throw new IllegalArgumentException("Unsupported user type: " + userType);
            };
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user type: " + request.getUserType());
        }
    }

    private BaseUser saveUser(BaseUser user) {
        return switch (user.getUserType()) {
            case ARTISAN -> artisanRepository.save((Artisan) user);
            case BUYER -> buyerRepository.save((Buyer) user);
            case ADMIN -> userRepository.save(user);
        };
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        validateLoginRequest(request);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            BaseUser user = findUserByEmail(request.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

            updateLastLoginDate(user);
            String token = tokenProvider.generateToken(userDetails);

            return createAuthResponse(user, token);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    private void validateLoginRequest(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
    }

    private void updateLastLoginDate(BaseUser user) {
        user.setLastLoginDate(LocalDateTime.now());
        saveUser(user);
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

        return buyerRepository.findByEmail(email).map(buyer -> buyer);
    }

    private AuthResponse createAuthResponse(BaseUser user, String token) {
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setUserType(user.getUserType().name());
        response.setProfImg(user.getProfilePictureUrl());

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