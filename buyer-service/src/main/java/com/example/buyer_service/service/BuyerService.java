package com.example.buyer_service.service;

import com.example.buyer_service.exception.BuyerNotFoundException;
import com.example.buyer_service.models.Buyer;
import com.example.buyer_service.repository.BuyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuyerService {
    @Autowired
    private BuyerRepository buyerRepository;


    public Buyer createBuyer(Buyer buyer) {
        return buyerRepository.save(buyer);
    }

    public Buyer findById(String buyerId){
        return buyerRepository.findById(buyerId)
                .orElseThrow(()-> new BuyerNotFoundException(buyerId));
    }
}
