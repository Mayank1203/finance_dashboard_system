package com.finance.entity;

import com.finance.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_records",
        indexes = {
                @Index(name = "idx_date",     columnList = "date"),
                @Index(name = "idx_type",     columnList = "type"),
                @Index(name = "idx_category", columnList = "category"),
                @Index(name = "idx_deleted",  columnList = "deleted")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType type;          // INCOME | EXPENSE

    @Column(nullable = false, length = 100)
    private String category;              // e.g. Salary, Rent, Food

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 500)
    private String notes;

    // Soft delete — record is hidden but not physically removed
    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

