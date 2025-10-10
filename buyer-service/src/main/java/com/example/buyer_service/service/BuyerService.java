package com.example.buyer_service.service;

import com.example.buyer_service.client.TicketClient;
import com.example.buyer_service.dtos.BuyerDTO;
import com.example.buyer_service.dtos.BuyerWithAmount;
import com.example.buyer_service.dtos.TicketDTO;
import com.example.buyer_service.exception.BuyerNotFoundException;
import com.example.buyer_service.mapper.BuyerMapper;
import com.example.buyer_service.models.Buyer;
import com.example.buyer_service.repository.BuyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuyerService {
    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private BuyerMapper buyerMapper;

    @Autowired
    private TicketClient ticketClient;

    //----CRUD OPERATIONS------
    public Buyer createBuyer(Buyer buyer) {
        return buyerRepository.save(buyer);
    }

    public Buyer findById(String buyerId){
        return buyerRepository.findById(buyerId)
                .orElseThrow(()-> new BuyerNotFoundException(buyerId));
    }

    public List<Buyer> findAll(){
        return buyerRepository.findAll();
    }

    public void removeBuyer(String buyerId){
        Buyer buyer= findById(buyerId);
        buyerRepository.delete(buyer);
    }

    public BuyerDTO changeBuyer(String buyerId, BuyerDTO buyerDTO){
        Buyer buyer= findById(buyerId);

        buyerMapper.updateFromDTO(buyerDTO, buyer);
        Buyer saved= buyerRepository.save(buyer);

        return buyerMapper.toDto(saved);
    }

    //------ OTHER OPERATIONS ---------
/// Calcular el gasto total de un comprador
    public BuyerWithAmount findBuyerWithTotalAmount(String buyerId){
        //Traer el buyer y sus datos:
        Buyer buyer= findById(buyerId);
        //Traer todos los tickets del ticket-service:
        List<TicketDTO> tickets= ticketClient.getByBuyerId(buyerId);

        Double totalPrice = tickets.stream()
                .mapToDouble(TicketDTO::getPrice)
                .sum();

        return BuyerWithAmount.builder()
                .buyerId(buyerId)
                .firstName(buyer.getFirstName())
                .lastName(buyer.getLastName())
                .totalAmountSpent(totalPrice)
                .build();

    }



}
