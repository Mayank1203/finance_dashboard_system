package com.finance.controller;


import com.finance.dto.response.*;
import com.finance.service.impl.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ANALYST')")   // VIEWER is excluded
@Tag(name = "Dashboard & Analytics", description = "Summary APIs for dashboard — ADMIN and ANALYST only")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @Operation(summary = "Full dashboard summary",
            description = "Returns total income, expenses, net balance, category totals, monthly trends, and 10 most recent records.")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getSummary()));
    }

    @GetMapping("/income")
    @Operation(summary = "Total income (optionally filtered by date range)")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalIncome(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getTotalIncome(from, to)));
    }

    @GetMapping("/expenses")
    @Operation(summary = "Total expenses (optionally filtered by date range)")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalExpenses(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getTotalExpenses(from, to)));
    }

    @GetMapping("/net-balance")
    @Operation(summary = "Net balance = total income - total expenses")
    public ResponseEntity<ApiResponse<BigDecimal>> getNetBalance() {
        BigDecimal income   = dashboardService.getTotalIncome(null, null);
        BigDecimal expenses = dashboardService.getTotalExpenses(null, null);
        return ResponseEntity.ok(ApiResponse.ok(income.subtract(expenses)));
    }

    @GetMapping("/categories")
    @Operation(summary = "Total amount grouped by category and type")
    public ResponseEntity<ApiResponse<List<CategoryTotalResponse>>> getCategoryTotals() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getCategoryTotals()));
    }

    @GetMapping("/trends/monthly")
    @Operation(summary = "Monthly income and expense totals",
            description = "Returns monthly totals for the last N months (default 12).")
    public ResponseEntity<ApiResponse<List<MonthlyTrendResponse>>> getMonthlyTrends(
            @RequestParam(defaultValue = "12") int months) {
        if (months < 1 || months > 24) {
            throw new IllegalArgumentException("months must be between 1 and 24");
        }
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getMonthlyTrends(months)));
    }
}


