package com.finance.repository.projection;

import com.finance.enums.TransactionType;
import java.math.BigDecimal;

public interface CategoryTotal {
    String getCategory();
    TransactionType getType();
    BigDecimal getTotal();
}

