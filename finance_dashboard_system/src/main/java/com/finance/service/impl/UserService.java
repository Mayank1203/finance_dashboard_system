package com.finance.service.impl;

import com.finance.dto.request.UpdateUserRequest;
import com.finance.dto.response.UserResponse;
import com.finance.entity.User;
import com.finance.exception.ResourceNotFoundException;
import com.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // ── List all users ────────────────────────────────────────────────────
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Get single user ───────────────────────────────────────────────────
    public UserResponse getUserById(Long id) {
        return toResponse(findOrThrow(id));
    }

    // ── Update user (name, role, active status) ───────────────────────────
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest req) {
        User user = findOrThrow(id);
        if (req.getName()   != null) user.setName(req.getName());
        if (req.getRole()   != null) user.setRole(req.getRole());
        if (req.getActive() != null) user.setActive(req.getActive());

        return toResponse(userRepository.save(user));
    }

    // ── Soft deactivate user ──────────────────────────────────────────────
    @Transactional
    public UserResponse deactivateUser(Long id) {
        User user = findOrThrow(id);
        user.setActive(false);
        return toResponse(userRepository.save(user));
    }

    // ── Reactivate user ───────────────────────────────────────────────────
    @Transactional
    public UserResponse activateUser(Long id) {
        User user = findOrThrow(id);
        user.setActive(true);
        return toResponse(userRepository.save(user));
    }

    // ── Hard delete (admin only, use with caution) ────────────────────────
    @Transactional
    public void deleteUser(Long id) {
        User user = findOrThrow(id);
        userRepository.delete(user);
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .role(u.getRole())
                .active(u.isActive())
                .createdAt(u.getCreatedAt())
                .build();
    }
}

