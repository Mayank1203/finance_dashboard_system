package com.finance.dto.response;


import com.finance.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MonthlyTrendResponse {
    private int year;
    private int month;
    private String monthName;     // e.g. "January"
    private TransactionType type;
    private BigDecimal total;
}


