package com.tafeco.DTO.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSumReportDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalSum;
    private List<OrderReportItemDTO> orders;

}

