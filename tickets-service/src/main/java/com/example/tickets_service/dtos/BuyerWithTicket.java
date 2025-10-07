package com.example.tickets_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerWithTicket {
    private String ticketId;
    private String gameId;
    private BuyerDTO buyerDTO;
}
