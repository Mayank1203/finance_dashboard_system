package com.finance.service.impl;

import com.finance.dto.response.*;
import com.finance.enums.TransactionType;
import com.finance.repository.FinancialRecordRepository;
import com.finance.repository.projection.CategoryTotal;
import com.finance.repository.projection.MonthlyTrend;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository recordRepository;
    private final FinancialRecordService    recordService;

    // ── Full dashboard summary ────────────────────────────────────────────
    public DashboardSummaryResponse getSummary() {
        BigDecimal totalIncome   = recordRepository.sumByType(TransactionType.INCOME);
        BigDecimal totalExpenses = recordRepository.sumByType(TransactionType.EXPENSE);
        BigDecimal netBalance    = totalIncome.subtract(totalExpenses);

        List<CategoryTotalResponse> categoryTotals = recordRepository.categoryTotals()
                .stream()
                .map(this::toCategoryTotalResponse)
                .toList();

        // Monthly trend — last 12 months
        LocalDate from = LocalDate.now().minusMonths(11).withDayOfMonth(1);
        List<MonthlyTrendResponse> monthlyTrends = recordRepository.monthlyTrend(from)
                .stream()
                .map(this::toMonthlyTrendResponse)
                .toList();

        List<RecordResponse> recentActivity = recordRepository
                .findRecentActivity(PageRequest.of(0, 10))
                .stream()
                .map(recordService::toResponse)
                .toList();

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .categoryTotals(categoryTotals)
                .monthlyTrends(monthlyTrends)
                .recentActivity(recentActivity)
                .build();
    }

    // ── Income summary ────────────────────────────────────────────────────
    public BigDecimal getTotalIncome(LocalDate from, LocalDate to) {
        if (from != null && to != null) {
            return recordRepository.sumByTypeAndDateRange(TransactionType.INCOME, from, to);
        }
        return recordRepository.sumByType(TransactionType.INCOME);
    }

    // ── Expense summary ───────────────────────────────────────────────────
    public BigDecimal getTotalExpenses(LocalDate from, LocalDate to) {
        if (from != null && to != null) {
            return recordRepository.sumByTypeAndDateRange(TransactionType.EXPENSE, from, to);
        }
        return recordRepository.sumByType(TransactionType.EXPENSE);
    }

    // ── Category totals ───────────────────────────────────────────────────
    public List<CategoryTotalResponse> getCategoryTotals() {
        return recordRepository.categoryTotals()
                .stream()
                .map(this::toCategoryTotalResponse)
                .toList();
    }

    // ── Monthly trends ────────────────────────────────────────────────────
    public List<MonthlyTrendResponse> getMonthlyTrends(int months) {
        LocalDate from = LocalDate.now().minusMonths(months - 1L).withDayOfMonth(1);
        return recordRepository.monthlyTrend(from)
                .stream()
                .map(this::toMonthlyTrendResponse)
                .toList();
    }

    // ── Mappers ───────────────────────────────────────────────────────────
    private CategoryTotalResponse toCategoryTotalResponse(CategoryTotal ct) {
        return CategoryTotalResponse.builder()
                .category(ct.getCategory())
                .type(ct.getType())
                .total(ct.getTotal())
                .build();
    }

    private MonthlyTrendResponse toMonthlyTrendResponse(MonthlyTrend mt) {
        return MonthlyTrendResponse.builder()
                .year(mt.getYear())
                .month(mt.getMonth())
                .monthName(Month.of(mt.getMonth()).name())
                .type(mt.getType())
                .total(mt.getTotal())
                .build();
    }
}


