package com.finance.dto.response;
import com.finance.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long          id;
    private String        name;
    private String        email;
    private Role          role;
    private boolean       active;
    private LocalDateTime createdAt;
}

