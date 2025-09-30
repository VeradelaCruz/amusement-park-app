package com.example.tickets_service.service;

import com.example.tickets_service.client.GameClient;
import com.example.tickets_service.dtos.GameDTO;
import com.example.tickets_service.dtos.TicketDTO;
import com.example.tickets_service.exception.GameNotAvailableException;
import com.example.tickets_service.exception.TicketNotFoundException;
import com.example.tickets_service.mapper.TicketMapper;
import com.example.tickets_service.models.Ticket;
import com.example.tickets_service.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private GameClient gameClient;

    @Autowired
    private TicketMapper ticketMapper;

    //-----CRUD OPERATIONS ------

    public Ticket findById(String ticketId){
        return ticketRepository.findById(ticketId)
                .orElseThrow(()-> new TicketNotFoundException(ticketId));
    }

    public List<Ticket> findAll(){
        return ticketRepository.findAll();
    }

    //Verificar que una entrada va a ser valida para
    // la venta teniendo en cuenta el horario del juego
    //Crea un ticket en la venta misma
    public TicketDTO sellTicket(String gameId, String buyerId) {
        GameDTO gameDTO = gameClient.getById(gameId);

        LocalTime now = LocalTime.now();
        if (now.isAfter(gameDTO.getStartTime()) && now.isBefore(gameDTO.getEndTime())) {
            Ticket ticket = new Ticket();
            ticket.setGameId(gameId);
            ticket.setBuyerId(buyerId);
            ticket.setTime(now);
            ticket.setDate(LocalDate.now());
            ticket.setPrice(gameDTO.getPriceGame());


            Ticket saved = ticketRepository.save(ticket);

            return ticketMapper.toDTO(saved,gameDTO);
        } else {
            throw new GameNotAvailableException(
                    "Game: " + gameDTO.getNameGame() + " is not available at this time"
            );
        }
    }

    public void removeTicket(String ticketId){
        Ticket ticket= findById(ticketId);
        ticketRepository.deleteById(ticket.getTicketId());
    }

    public TicketDTO changeTicket(String ticketId, TicketDTO ticketDTO){
        Ticket existing = findById(ticketId);
        ticketMapper.updateFromDTO(ticketDTO, existing);
        Ticket saved= ticketRepository.save(existing);

        return ticketMapper.toDTO(saved);
    }


}
