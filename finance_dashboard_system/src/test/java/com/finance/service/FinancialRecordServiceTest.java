package com.finance.service;


import com.finance.dto.request.RecordRequest;
import com.finance.dto.response.RecordResponse;
import com.finance.entity.FinancialRecord;
import com.finance.entity.User;
import com.finance.enums.Role;
import com.finance.enums.TransactionType;
import com.finance.exception.ResourceNotFoundException;
import com.finance.repository.FinancialRecordRepository;
import com.finance.repository.UserRepository;
import com.finance.service.impl.FinancialRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialRecordServiceTest {

    @Mock FinancialRecordRepository recordRepository;
    @Mock UserRepository            userRepository;

    @InjectMocks FinancialRecordService service;

    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .id(1L).name("Admin").email("admin@finance.com")
                .role(Role.ADMIN).active(true).build();
        // mock SecurityContext so currentUser() works
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin@finance.com");
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);

        when(userRepository.findByEmail("admin@finance.com")).thenReturn(Optional.of(adminUser));
    }

    @Test
    void create_savesAndReturnsResponse() {
        RecordRequest req = new RecordRequest();
        req.setAmount(new BigDecimal("1500.00"));
        req.setType(TransactionType.INCOME);
        req.setCategory("Salary");
        req.setDate(LocalDate.now());
        req.setNotes("Monthly salary");

        FinancialRecord saved = FinancialRecord.builder()
                .id(10L).amount(req.getAmount()).type(req.getType())
                .category(req.getCategory()).date(req.getDate())
                .notes(req.getNotes()).createdBy(adminUser).deleted(false).build();

        when(recordRepository.save(any())).thenReturn(saved);

        RecordResponse resp = service.create(req);

        assertThat(resp.getId()).isEqualTo(10L);
        assertThat(resp.getAmount()).isEqualByComparingTo("1500.00");
        assertThat(resp.getType()).isEqualTo(TransactionType.INCOME);
        assertThat(resp.getCategory()).isEqualTo("Salary");
    }

    @Test
    void getById_notFound_throws() {
        when(recordRepository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_softDeletesRecord() {
        FinancialRecord record = FinancialRecord.builder()
                .id(5L).deleted(false).createdBy(adminUser).build();
        when(recordRepository.findByIdAndDeletedFalse(5L)).thenReturn(Optional.of(record));

        service.delete(5L);

        assertThat(record.isDeleted()).isTrue();
        verify(recordRepository).save(record);
    }
}

