package com.example.buyer_service.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuyerRankingDTO {
    private String buyerId;
    private String firstName;
    private String lastName;
    private long ticketsCount;
}
