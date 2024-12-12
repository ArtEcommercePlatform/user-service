package com.artztall.user_service.controller;

import com.artztall.user_service.dto.AuthResponse;
import com.artztall.user_service.dto.LoginRequest;
import com.artztall.user_service.dto.SignupRequest;
import com.artztall.user_service.security.JwtTokenProvider;
import com.artztall.user_service.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;


@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private Faker faker;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;


    @BeforeEach
    void setUp() {
        faker = new Faker();
    }

    @Nested
    class SignupTests {
        @Test
        @DisplayName("Successful Signup for Artisan")
        void testSuccessfulArtisanSignup() throws Exception {
            // Prepare signup request
            SignupRequest signupRequest = createValidArtisanSignupRequest();

            // Prepare mock auth response
            AuthResponse mockAuthResponse = createMockAuthResponse(signupRequest, "ARTISAN");

            // Mock service method
            when(authService.signup(any(SignupRequest.class)))
                    .thenReturn(mockAuthResponse);

            // Perform request and validate
            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(signupRequest.getEmail()))
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("Successful Signup for Buyer")
        void testSuccessfulBuyerSignup() throws Exception {
            // Prepare signup request
            SignupRequest signupRequest = createValidBuyerSignupRequest();

            // Prepare mock auth response
            AuthResponse mockAuthResponse = createMockAuthResponse(signupRequest, "BUYER");

            // Mock service method
            when(authService.signup(any(SignupRequest.class)))
                    .thenReturn(mockAuthResponse);

            // Perform request and validate
            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(signupRequest.getEmail()))
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("Signup with Invalid Email")
        void testSignupWithInvalidEmail() throws Exception {
            SignupRequest signupRequest = createValidArtisanSignupRequest();
            signupRequest.setEmail("invalid-email");

            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors[0].field").value("email"));
        }

        @Test
        @DisplayName("Signup with Short Password")
        void testSignupWithShortPassword() throws Exception {
            SignupRequest signupRequest = createValidArtisanSignupRequest();
            signupRequest.setPassword("short");

            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors[0].field").value("password"));
        }
    }

    @Nested
    class LoginTests {
        @Test
        @DisplayName("Successful Login")
        void testSuccessfulLogin() throws Exception {
            // Prepare login request
            LoginRequest loginRequest = createValidLoginRequest();

            // Prepare mock auth response
            AuthResponse mockAuthResponse = createMockAuthResponseForLogin(loginRequest);

            // Mock service method
            when(authService.login(any(LoginRequest.class)))
                    .thenReturn(mockAuthResponse);

            // Perform request and validate
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(loginRequest.getEmail()))
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("Login with Invalid Email")
        void testLoginWithInvalidEmail() throws Exception {
            LoginRequest loginRequest = createValidLoginRequest();
            loginRequest.setEmail("invalid-email");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors[0].field").value("email"));
        }

        @Test
        @DisplayName("Login with Empty Password")
        void testLoginWithEmptyPassword() throws Exception {
            LoginRequest loginRequest = createValidLoginRequest();
            loginRequest.setPassword("");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors[0].field").value("password"));
        }
    }

    // Helper methods for creating test data
    private SignupRequest createValidArtisanSignupRequest() {
        SignupRequest request = new SignupRequest();
        request.setName(faker.name().fullName());
        request.setEmail(faker.internet().emailAddress());
        request.setPassword(faker.internet().password(8, 20));
        request.setPhoneNumber(faker.phoneNumber().phoneNumber());
        request.setUserType("ARTISAN");
        request.setBio(faker.lorem().sentence());
        request.setArtworkCategories(
                List.of(faker.commerce().department(), faker.commerce().department())
        );
        return request;
    }

    private SignupRequest createValidBuyerSignupRequest() {
        SignupRequest request = new SignupRequest();
        request.setName(faker.name().fullName());
        request.setEmail(faker.internet().emailAddress());
        request.setPassword(faker.internet().password(8, 20));
        request.setPhoneNumber(faker.phoneNumber().phoneNumber());
        request.setUserType("BUYER");
        return request;
    }

    private LoginRequest createValidLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setEmail(faker.internet().emailAddress());
        request.setPassword(faker.internet().password(8, 20));
        return request;
    }

    private AuthResponse createMockAuthResponse(SignupRequest request, String userType) {
        AuthResponse response = new AuthResponse();
        response.setToken(faker.random().hex(20)); // Generate a random token
        response.setId(faker.random().hex(10)); // Generate a random ID
        response.setEmail(request.getEmail());
        response.setName(request.getName());
        response.setUserType(userType);

        if (userType.equals("ARTISAN")) {
            response.setBio(request.getBio());
            response.setArtworkCategories(request.getArtworkCategories());
            response.setVerified(false);
        }

        return response;
    }

    private AuthResponse createMockAuthResponseForLogin(LoginRequest request) {
        AuthResponse response = new AuthResponse();
        response.setToken(faker.random().hex(20)); // Generate a random token
        response.setId(faker.random().hex(10)); // Generate a random ID
        response.setEmail(request.getEmail());
        response.setName(faker.name().fullName());
        response.setUserType("ARTISAN");
        return response;
    }
}