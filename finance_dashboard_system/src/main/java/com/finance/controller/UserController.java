package com.finance.controller;

import com.finance.dto.request.UpdateUserRequest;
import com.finance.dto.response.ApiResponse;
import com.finance.dto.response.UserResponse;
import com.finance.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")          // entire controller — ADMIN only
@Tag(name = "User Management", description = "ADMIN only — manage users and roles")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "List all users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getAllUsers()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user name, role, or active status")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,

            @Valid @RequestBody UpdateUserRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("User updated", userService.updateUser(id, req)));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a user account (soft disable)")
    public ResponseEntity<ApiResponse<UserResponse>> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("User deactivated", userService.deactivateUser(id)));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Reactivate a disabled user account")
    public ResponseEntity<ApiResponse<UserResponse>> activate(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("User activated", userService.activateUser(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Permanently delete a user")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.ok("User deleted"));
    }
}

