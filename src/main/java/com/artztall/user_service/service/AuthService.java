package com.artztall.user_service.service;

import com.artztall.user_service.dto.AuthResponse;
import com.artztall.user_service.dto.LoginRequest;
import com.artztall.user_service.dto.SignupRequest;
import com.artztall.user_service.model.BaseUser;
import com.artztall.user_service.model.Artisan;
import com.artztall.user_service.model.Buyer;
import com.artztall.user_service.model.UserType;
import com.artztall.user_service.repository.UserRepository;
import com.artztall.user_service.security.JwtTokenProvider;
import com.artztall.user_service.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already taken");
        }

        BaseUser user;
        if (request.getUserType().equals(UserType.ARTISAN.name())) {
            Artisan artisan = new Artisan();
            artisan.setBio(request.getBio());
            artisan.setArtworkCategories(request.getArtworkCategories());
            artisan.setAverageRating(0.0);
            artisan.setTotalSales(0);
            artisan.setVerified(false);
            user = artisan;
        } else {
            Buyer buyer = new Buyer();
            buyer.setAddresses(request.getAddresses());
            buyer.setFavoriteArtisans(new ArrayList<>());
            buyer.setRecentlyViewedProducts(new ArrayList<>());
            user = buyer;
        }

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setJoinDate(LocalDateTime.now());
        user.setUserType(UserType.valueOf(request.getUserType()));
        user.setActive(true);

        BaseUser savedUser = userRepository.save(user);
        String token = tokenProvider.generateToken(new UserDetailsImpl(savedUser));

        return createAuthResponse(savedUser, token);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String token = tokenProvider.generateToken(userDetails);
        BaseUser user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);

        return createAuthResponse(user, token);
    }

    private AuthResponse createAuthResponse(BaseUser user, String token) {
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setUserType(user.getUserType().name());
        return response;
    }
}