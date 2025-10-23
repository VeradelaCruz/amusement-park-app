package com.example.buyer_service.service;

import com.example.buyer_service.client.TicketClient;
import com.example.buyer_service.dtos.BuyerDTO;
import com.example.buyer_service.dtos.BuyerRankingDTO;
import com.example.buyer_service.dtos.BuyerWithAmount;
import com.example.buyer_service.dtos.TicketDTO;
import com.example.buyer_service.exception.BuyerNotFoundException;
import com.example.buyer_service.mapper.BuyerMapper;
import com.example.buyer_service.models.Buyer;
import com.example.buyer_service.repository.BuyerRepository;
import com.example.buyer_service.service.kafka.BuyerProducer;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;


@Service
public class BuyerService {
    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private BuyerMapper buyerMapper;

    @Autowired
    private TicketClient ticketClient;

    @Autowired
    private BuyerProducer buyerProducer;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    //----CRUD OPERATIONS------
    //El propósito del caché es evitar consultas repetidas a
    // la base de datos para los mismos datos.
    //Pero createBuyer() inserta un nuevo registro, no lo lee.
    public Buyer createBuyer(Buyer buyer) {
        Buyer savedBuyer= buyerRepository.save(buyer);
        //Enviar evento a Kafka
        buyerProducer.sendBuyerEvent(savedBuyer.getBuyerId());
        return savedBuyer;
    }



    @Cacheable(value = "buyers", key = "#buyerId", sync = true)
    public Buyer findById(String buyerId){
        System.out.println(">>> Llamando a la base de datos para Buyer " + buyerId);
        return buyerRepository.findById(buyerId)
                .orElseThrow(()-> new BuyerNotFoundException(buyerId));
    }


    //El value es el nombre de la caché (como el nombre de una colección o tabla).
    //Todas las operaciones (@Cacheable, @CachePut, @CacheEvict) que manipulen los mismos
    // datos deben usar el mismo value, para que trabajen sobre el mismo conjunto de elementos.
    @Cacheable(value = "buyers")
    public List<Buyer> findAll(){
        return buyerRepository.findAll();
    }


    @CacheEvict(value = "buyers", key = "#buyerId")
    public void removeBuyer(String buyerId){
        Buyer buyer= findById(buyerId);
        buyerRepository.deleteById(buyer.getBuyerId());
    }


    @CachePut(value = "buyers", key = "#buyerId")
    public BuyerDTO changeBuyer(String buyerId, BuyerDTO buyerDTO){
        Buyer buyer= findById(buyerId);

        buyerMapper.updateFromDTO(buyerDTO, buyer);
        Buyer saved= buyerRepository.save(buyer);
        // Usamos el producer en lugar de kafkaTemplate directamente
        buyerProducer.sendBuyerUpdateEvent(saved.getBuyerId(), saved.getFirstName());
        return buyerMapper.toDto(saved);
    }

    //------ OTHER OPERATIONS ---------
/// Calcular el gasto total de un comprador
    @Cacheable(value = "buyerTotalAmount", key = "#buyerId")
    public BuyerWithAmount findBuyerWithTotalAmount(String buyerId){
        //Traer el buyer y sus datos:
        Buyer buyer= findById(buyerId);
        //Traer todos los tickets del ticket-service:
        List<TicketDTO> tickets= ticketClient.getByBuyerId(buyerId);
        //Calcular el total del precio
        Double totalPrice = tickets.stream()
                .mapToDouble(TicketDTO::getPrice)
                .sum();
        //Armar el dto
        return BuyerWithAmount.builder()
                .buyerId(buyerId)
                .firstName(buyer.getFirstName())
                .lastName(buyer.getLastName())
                .totalAmountSpent(totalPrice)
                .build();

    }
/// Ranking de buyers por cantidad de tickets
@Cacheable(value = "buyerRanking")
public List<BuyerRankingDTO> getBuyerRanking() {
    // Traer todos los buyers
    List<Buyer> buyers = findAll();

    // Para cada buyer, llamar al ticketClient, contar tickets y construir el dto
    return buyers.stream()
            .map(buyer -> {
                long count = ticketClient.getByBuyerId(buyer.getBuyerId()).size();
                return BuyerRankingDTO.builder()
                        .buyerId(buyer.getBuyerId())
                        .firstName(buyer.getFirstName())
                        .lastName(buyer.getLastName())
                        .ticketsCount(count)
                        .build();
            })
            // Ordenar de mayor a menor
            .sorted(Comparator.comparingLong(BuyerRankingDTO::getTicketsCount).reversed())
            .toList();
}



}
