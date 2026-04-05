package com.finance.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardSummaryResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;             // income - expenses

    private List<CategoryTotalResponse>  categoryTotals;
    private List<MonthlyTrendResponse>   monthlyTrends;
    private List<RecordResponse>         recentActivity;
}


