package com.finance.dto.request;


import com.finance.enums.TransactionType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class RecordFilterRequest {

    private TransactionType type;       // null = both

    private String category;            // null = all categories

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    private String search;              // free-text search on notes/category
}

