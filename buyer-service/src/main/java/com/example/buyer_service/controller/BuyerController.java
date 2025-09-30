package com.example.buyer_service.controller;

import com.example.buyer_service.dtos.BuyerDTO;
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

    @DeleteMapping("/delete/{buyerId}")
    public ResponseEntity<?> deleteBuyer(@PathVariable String buyerId){
        buyerService.removeBuyer(buyerId);
        return ResponseEntity.ok().body("Buyer removed successfully");
    }

    @PutMapping("/update/{buyerId}")
    public ResponseEntity<?> updateBuyer(
            @PathVariable String buyerId,
            @RequestBody BuyerDTO buyerDTO){
        BuyerDTO updated= buyerService.changeBuyer(buyerId, buyerDTO);
        return ResponseEntity.ok(updated);
    }
}
