package com.smartpaymentsystem.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class MonthlyRevenueResponseDTO {
    private List<MonthlyRevenueDTO> points;
}
