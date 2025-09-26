package com.example.buyer_service.repository;

import com.example.buyer_service.models.Buyer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BuyerRepository extends MongoRepository<Buyer, String> {
}
