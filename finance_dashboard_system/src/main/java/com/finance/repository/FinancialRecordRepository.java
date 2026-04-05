package com.finance.repository;

import com.finance.entity.FinancialRecord;
import com.finance.enums.TransactionType;
import com.finance.repository.projection.CategoryTotal;
import com.finance.repository.projection.MonthlyTrend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialRecordRepository
        extends JpaRepository<FinancialRecord, Long>,
        JpaSpecificationExecutor<FinancialRecord> {

    // ── Single record lookup (exclude soft-deleted) ──────────────────────
    Optional<FinancialRecord> findByIdAndDeletedFalse(Long id);

    // ── Paged list (exclude soft-deleted) ────────────────────────────────
    Page<FinancialRecord> findAllByDeletedFalse(Pageable pageable);

    // ── Dashboard summary queries ────────────────────────────────────────
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r " +
            "WHERE r.type = :type AND r.deleted = false")
    BigDecimal sumByType(@Param("type") TransactionType type);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r " +
            "WHERE r.type = :type AND r.deleted = false " +
            "AND r.date BETWEEN :from AND :to")
    BigDecimal sumByTypeAndDateRange(@Param("type") TransactionType type,
                                     @Param("from") LocalDate from,
                                     @Param("to")   LocalDate to);

    // ── Category totals ──────────────────────────────────────────────────
    @Query("SELECT r.category AS category, r.type AS type, " +
            "SUM(r.amount) AS total " +
            "FROM FinancialRecord r " +
            "WHERE r.deleted = false " +
            "GROUP BY r.category, r.type " +
            "ORDER BY total DESC")
    List<CategoryTotal> categoryTotals();

    // ── Monthly trend (last N months) ────────────────────────────────────
    @Query("SELECT YEAR(r.date) AS year, MONTH(r.date) AS month, " +
            "r.type AS type, SUM(r.amount) AS total " +
            "FROM FinancialRecord r " +
            "WHERE r.deleted = false AND r.date >= :from " +
            "GROUP BY YEAR(r.date), MONTH(r.date), r.type " +
            "ORDER BY year ASC, month ASC")
    List<MonthlyTrend> monthlyTrend(@Param("from") LocalDate from);

    // ── Recent activity ──────────────────────────────────────────────────
    @Query("SELECT r FROM FinancialRecord r WHERE r.deleted = false " +
            "ORDER BY r.createdAt DESC")
    List<FinancialRecord> findRecentActivity(Pageable pageable);
}


