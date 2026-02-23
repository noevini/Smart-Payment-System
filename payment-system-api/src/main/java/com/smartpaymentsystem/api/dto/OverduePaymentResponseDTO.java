package com.smartpaymentsystem.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class OverduePaymentResponseDTO {
    private List<OverduePaymentDTO>  items;
}
