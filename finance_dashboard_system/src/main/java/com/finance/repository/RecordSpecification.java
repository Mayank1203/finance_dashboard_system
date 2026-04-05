package com.finance.repository;

import com.finance.dto.request.RecordFilterRequest;
import com.finance.entity.FinancialRecord;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RecordSpecification {

    private RecordSpecification() { }

    /**
     * Builds a dynamic JPA Specification from the filter criteria.
     * Only adds predicates for non-null filter values.
     */
    public static Specification<FinancialRecord> withFilters(RecordFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always exclude soft-deleted records
            predicates.add(cb.equal(root.get("deleted"), false));

            if (filter.getType() != null) {
                predicates.add(cb.equal(root.get("type"), filter.getType()));
            }

            if (filter.getCategory() != null && !filter.getCategory().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("category")),
                        "%" + filter.getCategory().toLowerCase() + "%"));
            }

            if (filter.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), filter.getDateFrom()));
            }

            if (filter.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), filter.getDateTo()));
            }

            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String pattern = "%" + filter.getSearch().toLowerCase() + "%";
                Predicate searchNotes    = cb.like(cb.lower(root.get("notes")),    pattern);
                Predicate searchCategory = cb.like(cb.lower(root.get("category")), pattern);
                predicates.add(cb.or(searchNotes, searchCategory));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}


