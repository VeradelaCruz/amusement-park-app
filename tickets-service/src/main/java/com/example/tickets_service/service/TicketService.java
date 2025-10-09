package com.example.tickets_service.service;

import com.example.tickets_service.client.BuyerClient;
import com.example.tickets_service.client.GameClient;
import com.example.tickets_service.dtos.*;
import com.example.tickets_service.exception.GameNotAvailableException;
import com.example.tickets_service.exception.TicketNotFoundException;
import com.example.tickets_service.mapper.TicketMapper;
import com.example.tickets_service.models.Ticket;
import com.example.tickets_service.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Comparator;
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

    @Autowired
    private BuyerClient buyerClient;

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
            ticket.setDate(LocalDate.now(ZoneId.of("UTC"))); // o la zona que uses
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
    public TicketCountDTO gameTicketAmount(String gameId, LocalDate date) {
               // 🔹 Contar tickets que caen dentro del rango
        long totalAmount = findAll().stream()
                .filter(t -> t.getGameId().equals(gameId))
                .filter(t -> t.getDate().equals(date))
                .count();

        // 🔹 Traer info del juego
        GameDTO gameDTO = gameClient.getById(gameId);

        // 🔹 Devolver DTO
        return new TicketCountDTO(totalAmount, date, gameDTO);
    }


    //Sumatoria total de los montos de ventas en un determinado día.
    public SalesTotalDTO countAllTickets(LocalDate date) {
        double totalAmount = findAll().stream()
                .filter(t -> t.getDate().equals(date))
                .mapToDouble(Ticket::getPrice)
                .sum();

        return new SalesTotalDTO(date, totalAmount);
    }



    //Sumatoria total de los montos de ventas en un determinado mes y año.
    public SalesTotalMonthYearDTO countByMonthAndYear(int month, int year){
        try {
            YearMonth ym = YearMonth.of(year, month);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid month or year");
        }

        double totalAmount = findAll().stream()
                .filter(ticket -> ticket.getDate().getMonthValue() == month &&
                        ticket.getDate().getYear() == year)
                //transforma cada elemento del stream en un valor de tipo double
                .mapToDouble(Ticket::getPrice)
                .sum();
        return new SalesTotalMonthYearDTO(month, year, totalAmount);
    }

    public BuyerWithTicket getTopBuyerWithTicket() {
        // 1️⃣ Agrupar todos los tickets por buyerId
        Map<String, List<Ticket>> ticketsByBuyer =findAll().stream()
                .collect(Collectors.groupingBy(Ticket::getBuyerId));

        if (ticketsByBuyer.isEmpty()) {
            return null; // o lanzar excepción si no hay tickets
        }

        // 2️⃣ Elegir al comprador top según cantidad de tickets y, en caso de empate, suma total
        Map.Entry<String, List<Ticket>> topBuyerEntry = ticketsByBuyer.entrySet().stream()
                .max(Comparator.comparingInt((Map.Entry<String, List<Ticket>> e) -> e.getValue().size()) // cantidad tickets
                        .thenComparingDouble(e -> e.getValue().stream().mapToDouble(Ticket::getPrice).sum()) // suma total
                )
                .orElseThrow(); // nunca debería ser vacío si hay tickets

        String topBuyerId = topBuyerEntry.getKey();
        List<Ticket> topBuyerTickets = topBuyerEntry.getValue();

        // 3️⃣ Llamar al microservicio de buyers
        BuyerDTO buyerDTO = buyerClient.getById(topBuyerId);

        // 4️⃣ Construir lista de GameDTOs para cada ticket
        List<GameDTO> gameDTOList = topBuyerTickets.stream()
                .map(ticket -> gameClient.getById(ticket.getGameId()))
                .toList();

        // 5️⃣ Construir el DTO final
        return BuyerWithTicket.builder()
                .buyerDTO(buyerDTO)
                .dtoList(gameDTOList)
                .build();
    }


    //Juego con la mayor cantidad de entradas vendidas hasta el día en que
    // se lleve a cabo la consulta.
    public GameAmountTickets getTopGameWithTicket() {
        // Agrupamos por juego y contamos los tickets
        Map<String, Long> gamesByTickets = findAll().stream()
                .collect(Collectors.groupingBy(Ticket::getGameId, Collectors.counting()));

        // Buscamos el juego con mayor cantidad de tickets
        Map.Entry<String, Long> topGameEntry = gamesByTickets.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(); // lanza excepción si no hay tickets

        String topGameId = topGameEntry.getKey();     // el ID del juego
        Long topAmount = topGameEntry.getValue();     // cantidad de tickets vendidos

        // Traemos el nombre del juego por Feign
        GameDTO gameDTO = gameClient.getById(topGameId);

        // Armamos el DTO de respuesta
        GameAmountTickets dto = new GameAmountTickets();
        dto.setAmount(topAmount);
        dto.setGameName(gameDTO.getNameGame());

        return dto;
    }

    //Promedio de precio de tickets vendidos por juego
    public List<GameWithAverageDTO> getAverageGame(){
        Map<String, Double> gamesWithPrices= findAll().stream()
                .collect(Collectors.groupingBy(Ticket::getGameId,
                        Collectors.averagingDouble(Ticket::getPrice)));

        return gamesWithPrices.entrySet().stream()
                .map(entry->
                {
                    String id= entry.getKey();
                    Double average= entry.getValue();
                    // buscar el nombre del juego con el gameId
                    GameDTO gameDTO = gameClient.getById(id);
                    //armar el dto
                    GameWithAverageDTO dto= new GameWithAverageDTO();
                    dto.setNameGame(gameDTO.getNameGame());
                    dto.setAverage(average);
                    return dto;
                })
                .toList();
    }


}
