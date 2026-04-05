package com.finance.dto.response;


import com.finance.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String tokenType;
    private String email;
    private String name;
    private Role   role;
}

