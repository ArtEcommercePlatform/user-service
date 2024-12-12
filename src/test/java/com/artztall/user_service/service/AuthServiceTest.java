    package com.artztall.user_service.service;


    import com.artztall.user_service.dto.AuthResponse;
    import com.artztall.user_service.dto.LoginRequest;
    import com.artztall.user_service.dto.SignupRequest;
    import com.artztall.user_service.exception.UserAlreadyExistsException;
    import com.artztall.user_service.exception.UserNotFoundException;
    import com.artztall.user_service.model.Artisan;
    import com.artztall.user_service.model.BaseUser;
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
    import org.springframework.security.authentication.BadCredentialsException;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.crypto.password.PasswordEncoder;

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
            SignupRequest request = new SignupRequest();
            request.setEmail("test@example.com");
            request.setPassword("password");
            request.setUserType("ARTISAN");
            request.setName("Test User");

            when(artisanRepository.existsByEmail(eq("test@example.com"))).thenReturn(false);
            when(userRepository.existsByEmail(eq("test@example.com"))).thenReturn(false);
            when(passwordEncoder.encode(eq("password"))).thenReturn("encoded_password");
            when(artisanRepository.save(any(Artisan.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(tokenProvider.generateToken(any(UserDetailsImpl.class))).thenReturn("test_token");

            AuthResponse response = authService.signup(request);

            assertNotNull(response);
            assertEquals("test@example.com", response.getEmail());
            assertEquals("Test User", response.getName());
            assertEquals("test_token", response.getToken());

            verify(artisanRepository, times(1)).save(any(Artisan.class));
        }

        @Test
        void testSignup_UserAlreadyExists() {
            SignupRequest request = new SignupRequest();
            request.setEmail("test@example.com");
            request.setPassword("password");
            request.setUserType("ARTISAN");

            when(userRepository.existsByEmail(eq("test@example.com"))).thenReturn(true);

            assertThrows(UserAlreadyExistsException.class, () -> authService.signup(request));

            verify(userRepository, times(1)).existsByEmail(eq("test@example.com"));
            verifyNoInteractions(passwordEncoder, tokenProvider);
        }

        @Test
        void testLogin_Success() {
            LoginRequest request = new LoginRequest();
            request.setEmail("test@example.com");
            request.setPassword("password");

            // Mock user details
            BaseUser user = new Artisan();
            user.setEmail("test@example.com");
            user.setPassword("encoded_password");
            user.setUserType(UserType.ARTISAN);

            UserDetailsImpl userDetails = new UserDetailsImpl(user);

            // Mock authentication process
            Authentication mockAuthentication = mock(Authentication.class);
            when(mockAuthentication.getPrincipal()).thenReturn(userDetails);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuthentication);

            // Mock repositories
            when(artisanRepository.findByEmail(eq("test@example.com"))).thenReturn(Optional.of((Artisan) user));

            // Mock token generation
            when(tokenProvider.generateToken(eq(userDetails))).thenReturn("test_token");

            // Perform login
            AuthResponse response = authService.login(request);

            // Assert results
            assertNotNull(response);
            assertEquals("test@example.com", response.getEmail());
            assertEquals("test_token", response.getToken());

            // Verify interactions
            verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(tokenProvider, times(1)).generateToken(eq(userDetails));
        }


        @Test
        void testLogin_UserNotFound() {
            LoginRequest request = new LoginRequest();
            request.setEmail("nonexistent@example.com");
            request.setPassword("password");

            // Mock repositories to simulate user not found
            when(artisanRepository.findByEmail(eq("nonexistent@example.com"))).thenReturn(Optional.empty());
            when(buyerRepository.findByEmail(eq("nonexistent@example.com"))).thenReturn(Optional.empty());

            // Mock successful authentication (you can mock this as you did for the user)
            Authentication mockAuthentication = mock(Authentication.class);
            when(mockAuthentication.getPrincipal()).thenReturn(mock(UserDetailsImpl.class));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuthentication);

            // Assert that the UserNotFoundException is thrown
            assertThrows(UserNotFoundException.class, () -> authService.login(request));

            // Verify repository interactions
            verify(artisanRepository, times(1)).findByEmail(eq("nonexistent@example.com"));
            verify(buyerRepository, times(1)).findByEmail(eq("nonexistent@example.com"));

            // Verify authentication manager interaction
            verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }


    }
