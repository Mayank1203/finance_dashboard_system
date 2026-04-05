package com.finance.service;

import com.finance.dto.request.LoginRequest;
import com.finance.dto.request.RegisterRequest;
import com.finance.dto.response.AuthResponse;
import com.finance.entity.User;
import com.finance.enums.Role;
import com.finance.exception.EmailAlreadyExistsException;
import com.finance.repository.UserRepository;
import com.finance.security.JwtService;
import com.finance.service.impl.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository        userRepository;
    @Mock PasswordEncoder       passwordEncoder;
    @Mock JwtService            jwtService;
    @Mock AuthenticationManager authManager;

    @InjectMocks AuthService authService;

    @Test
    void register_success() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Alice");
        req.setEmail("alice@example.com");
        req.setPassword("secret123");

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtService.generateToken("alice@example.com")).thenReturn("mock-token");

        AuthResponse resp = authService.register(req);

        assertThat(resp.getToken()).isEqualTo("mock-token");
        assertThat(resp.getEmail()).isEqualTo("alice@example.com");
        assertThat(resp.getRole()).isEqualTo(Role.VIEWER);
    }

    @Test
    void register_duplicateEmail_throws() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("exists@example.com");
        req.setPassword("pass");

        when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void login_success() {
        LoginRequest req = new LoginRequest();
        req.setEmail("alice@example.com");
        req.setPassword("secret");

        User user = User.builder()
                .id(1L).name("Alice").email("alice@example.com")
                .password("hashed").role(Role.VIEWER).active(true).build();
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("alice@example.com")).thenReturn("jwt-token");

        AuthResponse resp = authService.login(req);

        assertThat(resp.getToken()).isEqualTo("jwt-token");
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}


