package com.example.tickets_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesTotalMonthYearDTO {
    private int month;
    private int year;
    private double totalAmount;
}
