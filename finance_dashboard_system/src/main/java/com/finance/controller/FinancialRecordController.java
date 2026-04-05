package com.finance.controller;

import com.finance.dto.request.RecordFilterRequest;
import com.finance.dto.request.RecordRequest;
import com.finance.dto.response.ApiResponse;
import com.finance.dto.response.PagedResponse;
import com.finance.dto.response.RecordResponse;
import com.finance.service.impl.FinancialRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
@Tag(name = "Financial Records", description = "Create, view, update, delete and filter financial records")
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    // ── CREATE — ADMIN only ───────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new financial record [ADMIN]")
    public ResponseEntity<ApiResponse<RecordResponse>> create(
            @Valid @RequestBody RecordRequest req) {
        RecordResponse created = recordService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Record created", created));
    }

    // ── LIST with filter + pagination — ALL authenticated roles ───────────
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @Operation(summary = "List records with optional filters and pagination [ALL]",
            description = "Supports filtering by type, category, date range, and free-text search.")
    public ResponseEntity<ApiResponse<PagedResponse<RecordResponse>>> list(
            RecordFilterRequest filter,
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "20")   int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, Math.min(size, 100), sort);
        return ResponseEntity.ok(ApiResponse.ok(recordService.list(filter, pageable)));
    }

    // ── GET ONE — ALL authenticated roles ─────────────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @Operation(summary = "Get a single financial record by ID [ALL]")
    public ResponseEntity<ApiResponse<RecordResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(recordService.getById(id)));
    }

    // ── UPDATE — ADMIN only ───────────────────────────────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a financial record [ADMIN]")
    public ResponseEntity<ApiResponse<RecordResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody RecordRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Record updated", recordService.update(id, req)));
    }

    // ── SOFT DELETE — ADMIN only ──────────────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft-delete a financial record [ADMIN]",
            description = "Marks the record as deleted. It is hidden from all queries but not removed from the database.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        recordService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Record deleted"));
    }
}
