package com.example.tickets_service.client;

import com.example.tickets_service.dtos.BuyerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "buyer-service")
public interface BuyerClient {
    @GetMapping("buyer/byId/{buyerId}")
    BuyerDTO getById(@PathVariable String buyerId);
}
