package com.tafeco.DTO.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSumRangeReportDTO {
    private String range; // например, "0-100", "100-500"
    private Long count;

}
