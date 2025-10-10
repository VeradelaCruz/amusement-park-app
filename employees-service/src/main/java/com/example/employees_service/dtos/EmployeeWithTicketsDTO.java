package com.example.employees_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeWithTicketsDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String gameId;
    private String gameName;
    private long ticketsSoldInGame;
}
