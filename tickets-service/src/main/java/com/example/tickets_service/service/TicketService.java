package com.example.tickets_service.service;

import com.example.tickets_service.client.GameClient;
import com.example.tickets_service.dtos.GameDTO;
import com.example.tickets_service.dtos.TicketCountDTO;
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
import java.util.Map;
import java.util.stream.Collectors;

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

        // 🔹 Validación si no existe el juego
        if (gameDTO == null) {
            throw new GameNotAvailableException("Game not found with id: " + gameId);
        }

        LocalTime now = LocalTime.now();

        // 🔹 Validar si está dentro del rango [startTime, endTime]
        if (isGameAvailable(gameDTO, now)) {
            Ticket ticket = new Ticket();
            ticket.setGameId(gameId);
            ticket.setBuyerId(buyerId);
            ticket.setTime(now);
            ticket.setDate(LocalDate.now());
            ticket.setPrice(gameDTO.getPriceGame());

            Ticket saved = ticketRepository.save(ticket);

            // 🔹 Mapear ticket + juego
            return ticketMapper.toDTO(saved, gameDTO);
        } else {
            throw new GameNotAvailableException(gameDTO.getNameGame());
        }
    }

    private boolean isGameAvailable(GameDTO gameDTO, LocalTime now) {
        return (now.equals(gameDTO.getStartTime()) || now.isAfter(gameDTO.getStartTime())) &&
                (now.equals(gameDTO.getEndTime())   || now.isBefore(gameDTO.getEndTime()));
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

    //----------OTHER OPERATIONS-------
    //Cantidad de entradas vendidas en todos los juegos en una fecha
    public List<TicketCountDTO> ticketAmount(LocalDate date) {
        // 1️⃣ Filtrar solo tickets de la fecha ingresada
        List<Ticket> tickets = findAll().stream()
                .filter(t -> t.getDate().equals(date))
                .collect(Collectors.toList());

        // 2️⃣ Transformar el Map en una lista de DTOs
        Map<String, Long> gameAmount = tickets.stream()
                .collect(Collectors.groupingBy(Ticket::getGameId, Collectors.counting()));
        //3️⃣ Transformar a DTO y llamar Feign
        List<TicketCountDTO> result = gameAmount.entrySet().stream()
                .map(entry -> {
                    GameDTO gameDTO = gameClient.getById(entry.getKey());
                    return new TicketCountDTO(entry.getValue(),date, gameDTO);
                })
                .collect(Collectors.toList());
        return result;
    }

    //Cantidad de entradas vendidas para un determinado juego, en una fecha particular.
    public TicketCountDTO gameTicketAmount(String gameId, LocalDate date){
        //Todos los tickets filtrados por el id del juego ingresado
        // Contar las entradas por cada juego
        long totalAmount = findAll().stream()
                .filter(t-> t.getGameId().equals(gameId) && t.getDate().equals(date))
                .count();
        //Llamo al microservicio de juegos:
        GameDTO gameDTO = gameClient.getById(gameId);
        //Devuelvo el dto asignando el valor total, la fecha y el dto
        return new TicketCountDTO(totalAmount, date, gameDTO);
    }



}
