package com.finance.dto.response;


import com.finance.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CategoryTotalResponse {
    private String category;
    private TransactionType type;
    private BigDecimal total;
}
