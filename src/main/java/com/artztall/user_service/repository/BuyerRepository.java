package com.artztall.user_service.repository;

import com.artztall.user_service.model.Buyer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BuyerRepository extends MongoRepository<Buyer, String> {

}