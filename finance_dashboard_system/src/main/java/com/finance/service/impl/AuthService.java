package com.finance.service.impl;


import com.finance.dto.request.LoginRequest;
import com.finance.dto.request.RegisterRequest;
import com.finance.dto.response.AuthResponse;
import com.finance.entity.User;
import com.finance.exception.AccountDisabledException;
import com.finance.exception.EmailAlreadyExistsException;
import com.finance.repository.UserRepository;
import com.finance.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository       userRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtService           jwtService;
    private final AuthenticationManager authManager;

    // ── Register ──────────────────────────────────────────────────────────
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new EmailAlreadyExistsException(req.getEmail());
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole() != null ? req.getRole() : com.finance.enums.Role.VIEWER)
                .active(true)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return buildAuthResponse(user, token);
    }

    // ── Login ─────────────────────────────────────────────────────────────
    public AuthResponse login(LoginRequest req) {
        // AuthenticationManager validates credentials and throws on failure
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        User user = userRepository.findByEmail(req.getEmail()).orElseThrow();

        if (!user.isActive()) {
            throw new AccountDisabledException();
        }

        String token = jwtService.generateToken(user.getEmail());
        return buildAuthResponse(user, token);
    }

    // ── Helper ────────────────────────────────────────────────────────────
    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }
}


