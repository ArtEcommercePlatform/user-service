package com.artztall.user_service.repository;

import com.artztall.user_service.model.Artisan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtisanRepository extends MongoRepository<Artisan, String> {
    Optional<Artisan> findByEmail(String email);
    boolean existsByEmail(String email);
}