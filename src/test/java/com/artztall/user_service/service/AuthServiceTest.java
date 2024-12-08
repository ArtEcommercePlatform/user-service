package com.artztall.user_service.service;

import com.artztall.user_service.dto.AuthResponse;
import com.artztall.user_service.dto.LoginRequest;
import com.artztall.user_service.dto.SignupRequest;
import com.artztall.user_service.exception.UserAlreadyExistsException;
import com.artztall.user_service.exception.UserNotFoundException;
import com.artztall.user_service.model.Artisan;
import com.artztall.user_service.model.BaseUser;
import com.artztall.user_service.model.Buyer;
import com.artztall.user_service.model.UserType;
import com.artztall.user_service.repository.ArtisanRepository;
import com.artztall.user_service.repository.BuyerRepository;
import com.artztall.user_service.repository.UserRepository;
import com.artztall.user_service.security.JwtTokenProvider;
import com.artztall.user_service.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArtisanRepository artisanRepository;

    @Mock
    private BuyerRepository buyerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignup_Success() {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setName("Test User");
        request.setUserType("ARTISAN");
        request.setBio("Artist bio");

        Artisan artisan = new Artisan();
        artisan.setEmail(request.getEmail());
        artisan.setUserType(UserType.ARTISAN); // Set valid UserType

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(artisanRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(artisanRepository.save(any(Artisan.class))).thenReturn(artisan);
        when(tokenProvider.generateToken(any(UserDetailsImpl.class))).thenReturn("jwtToken");

        // Act
        AuthResponse response = authService.signup(request);

        // Assert
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("eyJhbGciOiJIUzM4NCJ9.eyJyb2xlIjoiUk9MRV9CVVlFUiIsImlkIjoiNjc0MWNlMDllODA2ZjU0NjhmNDZjY2JhIiwic3ViIjoiYXJ0aXNhbkBleGFtcGxlLmNvbSIsImlhdCI6MTczMzIwNDkxMywiZXhwIjoxNzMzMjkxMzEzfQ.yhAPVfg6GsAIj3ITzm-XBCE1JbijVKzm9HUxTOI7-kNTWDs_tkFF2f31hyRzKly5", response.getToken());
        verify(artisanRepository, times(1)).save(any(Artisan.class));
    }


    @Test
    void testSignup_EmailAlreadyTaken() {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setUserType("ARTISAN"); // Ensure this matches the validation logic

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> authService.signup(request));
        verify(artisanRepository, never()).save(any(Artisan.class));
    }


    @Test
    void testLogin_Success() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        BaseUser user = new Artisan();
        user.setEmail(request.getEmail());
        user.setUserType(UserType.ARTISAN); // Set valid UserType

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(artisanRepository.findByEmail(request.getEmail())).thenReturn(Optional.of((Artisan) user));
        when(tokenProvider.generateToken(any(UserDetailsImpl.class))).thenReturn("jwtToken");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("jwtToken", response.getToken());
        verify(artisanRepository, times(1)).findByEmail(request.getEmail());
        verify(tokenProvider, times(1)).generateToken(any(UserDetailsImpl.class));
    }


    @Test
    void testLogin_UserNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@example.com");
        request.setPassword("password123");

        when(artisanRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(buyerRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> authService.login(request));

        // Verify no interactions with authenticationManager
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }


    @Test
    void testLogin_InvalidCredentials() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.login(request));
    }
}
