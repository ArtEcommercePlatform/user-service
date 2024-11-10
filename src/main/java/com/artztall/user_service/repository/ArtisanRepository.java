package com.artztall.user_service.repository;

import com.artztall.user_service.model.Artisan;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArtisanRepository extends MongoRepository<Artisan, String> {

}
