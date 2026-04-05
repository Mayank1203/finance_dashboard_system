package com.finance.service.impl;
import com.finance.dto.request.RecordFilterRequest;
import com.finance.dto.request.RecordRequest;
import com.finance.dto.response.PagedResponse;
import com.finance.dto.response.RecordResponse;
import com.finance.entity.FinancialRecord;
import com.finance.entity.User;
import com.finance.exception.ResourceNotFoundException;
import com.finance.repository.FinancialRecordRepository;
import com.finance.repository.RecordSpecification;

import com.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository            userRepository;

    // ── Create ────────────────────────────────────────────────────────────
    @Transactional
    public RecordResponse create(RecordRequest req) {
        User caller = currentUser();
        FinancialRecord record = FinancialRecord.builder()
                .amount(req.getAmount())
                .type(req.getType())
                .category(req.getCategory().trim())
                .date(req.getDate())
                .notes(req.getNotes())
                .createdBy(caller)
                .deleted(false)
                .build();
        return toResponse(recordRepository.save(record));
    }

    // ── List (paged + filtered) ───────────────────────────────────────────
    public PagedResponse<RecordResponse> list(RecordFilterRequest filter, Pageable pageable) {
        Page<RecordResponse> page = recordRepository
                .findAll(RecordSpecification.withFilters(filter), pageable)
                .map(this::toResponse);
        return PagedResponse.of(page);
    }

    // ── Get one ───────────────────────────────────────────────────────────
    public RecordResponse getById(Long id) {
        return toResponse(findActiveOrThrow(id));
    }

    // ── Update ────────────────────────────────────────────────────────────
    @Transactional
    public RecordResponse update(Long id, RecordRequest req) {
        FinancialRecord record = findActiveOrThrow(id);
        record.setAmount(req.getAmount());
        record.setType(req.getType());
        record.setCategory(req.getCategory().trim());
        record.setDate(req.getDate());
        record.setNotes(req.getNotes());
        return toResponse(recordRepository.save(record));
    }

    // ── Soft delete ───────────────────────────────────────────────────────
    @Transactional
    public void delete(Long id) {
        FinancialRecord record = findActiveOrThrow(id);
        record.setDeleted(true);
        recordRepository.save(record);
    }

    // ── Recent activity ───────────────────────────────────────────────────
    public List<RecordResponse> recentActivity(Pageable pageable) {
        return recordRepository.findRecentActivity(pageable)
                .stream().map(this::toResponse).toList();
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private FinancialRecord findActiveOrThrow(Long id) {
        return recordRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("FinancialRecord", id));
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    public RecordResponse toResponse(FinancialRecord r) {
        return RecordResponse.builder()
                .id(r.getId())
                .amount(r.getAmount())
                .type(r.getType())
                .category(r.getCategory())
                .date(r.getDate())
                .notes(r.getNotes())
                .createdByName(r.getCreatedBy() != null ? r.getCreatedBy().getName() : null)
                .createdByEmail(r.getCreatedBy() != null ? r.getCreatedBy().getEmail() : null)
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}

