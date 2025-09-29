package com.example.tickets_service.service;

import com.example.tickets_service.client.GameClient;
import com.example.tickets_service.dtos.GameDTO;
import com.example.tickets_service.dtos.TicketDTO;
import com.example.tickets_service.exception.GameNotAvailableException;
import com.example.tickets_service.exception.TicketNotFoundException;
import com.example.tickets_service.models.Ticket;
import com.example.tickets_service.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private GameClient gameClient;

    public Ticket createTicket(Ticket ticket){
        return ticketRepository.save(ticket);
    }

    public Ticket findById(String ticketId){
        return ticketRepository.findById(ticketId)
                .orElseThrow(()-> new TicketNotFoundException(ticketId));
    }

    public List<Ticket> findAll(){
        return ticketRepository.findAll();
    }

    //Verificar que una entrada va a ser valida para
    // la venta teniendo en cuenta el horario del juego
    public Object sellTicket(String gameId) {
        //Llamo al servicio de juegos para traer el juego
        GameDTO gameDTO = gameClient.getById(gameId);
        //Armo una variable con la hora del momento
        LocalTime now = LocalTime.now();
        if (now.isAfter(gameDTO.getStartTime()) && now.isBefore(gameDTO.getEndTime())) {
            // Aquí crearías el ticket y lo devolverías
            return new TicketDTO();
        } else {
            throw new GameNotAvailableException("Game " + gameDTO.getNameGame() + " is not available at this time");
        }
    }

}
