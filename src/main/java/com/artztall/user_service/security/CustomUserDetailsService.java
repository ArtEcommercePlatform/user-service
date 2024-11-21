package com.artztall.user_service.security;

import com.artztall.user_service.model.BaseUser;
import com.artztall.user_service.model.Artisan;
import com.artztall.user_service.model.Buyer;
import com.artztall.user_service.repository.ArtisanRepository;
import com.artztall.user_service.repository.BuyerRepository;
import com.artztall.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ArtisanRepository artisanRepository;
    private final BuyerRepository buyerRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Attempting to load user by email: {}", email);

        try {
            // First try to find an Artisan
            Optional<Artisan> artisan = artisanRepository.findByEmail(email);
            if (artisan.isPresent()) {
                BaseUser user = artisan.get();
                validateUser(user);
                return new UserDetailsImpl(user);
            }

            // Then try to find a Buyer
            Optional<Buyer> buyer = buyerRepository.findByEmail(email);
            if (buyer.isPresent()) {
                BaseUser user = buyer.get();
                validateUser(user);
                return new UserDetailsImpl(user);
            }

            // If no user is found, throw exception
            log.warn("No user found with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);

        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during user authentication for email: {}", email, e);
            throw new UsernameNotFoundException("Error during authentication", e);
        }
    }

    private void validateUser(BaseUser user) {
        if (!user.isActive()) {
            log.warn("Attempt to login with inactive account: {}", user.getEmail());
            throw new UsernameNotFoundException("Account is not active");
        }
        log.debug("Successfully loaded user: {}", user.getEmail());
    }
}