package com.tafeco.DTO.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPeriodReportDTO {
    private String period; // например, "2025-07" или "2025-07-01"
    private Long count;
    private BigDecimal sum;

}
