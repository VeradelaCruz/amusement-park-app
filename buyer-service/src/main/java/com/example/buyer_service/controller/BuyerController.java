package com.example.buyer_service.controller;

import com.example.buyer_service.models.Buyer;
import com.example.buyer_service.service.BuyerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/buyer")
public class BuyerController {
    @Autowired
    private BuyerService buyerService;

    @PostMapping("/add")
    public ResponseEntity<?> addBuyer(@RequestBody Buyer buyer){
        return ResponseEntity.ok(buyerService.createBuyer(buyer));
    }

    @GetMapping("/byId/{buyerId}")
    public ResponseEntity<?> getById(@PathVariable String buyerId){
        return ResponseEntity.ok(buyerService.findById(buyerId));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(buyerService.findAll());
    }
}
