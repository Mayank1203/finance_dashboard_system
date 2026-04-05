package com.finance.repository.projection;
import com.finance.enums.TransactionType;
import java.math.BigDecimal;

public interface MonthlyTrend {
    Integer getYear();
    Integer getMonth();
    TransactionType getType();
    BigDecimal getTotal();
}


