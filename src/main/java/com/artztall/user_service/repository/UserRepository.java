package com.artztall.user_service.repository;



import com.artztall.user_service.model.BaseUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<BaseUser, String> {
    Optional<BaseUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
