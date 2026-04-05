package com.finance.config;

import com.finance.entity.User;
import com.finance.enums.Role;
import com.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds a default ADMIN user on first startup so the system is
 * immediately usable without manual DB inserts.
 *
 * Default credentials:
 *   email    : admin@finance.com
 *   password : Admin@123
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@finance.com")) {
            User admin = User.builder()
                    .name("System Admin")
                    .email("admin@finance.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);
            log.info("Default ADMIN user created — email: admin@finance.com | password: Admin@123");
        }

        // Seed a sample ANALYST and VIEWER for demo purposes
        if (!userRepository.existsByEmail("analyst@finance.com")) {
            userRepository.save(User.builder()
                    .name("Demo Analyst")
                    .email("analyst@finance.com")
                    .password(passwordEncoder.encode("Analyst@123"))
                    .role(Role.ANALYST)
                    .active(true)
                    .build());
            log.info("Demo ANALYST user created — email: analyst@finance.com | password: Analyst@123");
        }

        if (!userRepository.existsByEmail("viewer@finance.com")) {
            userRepository.save(User.builder()
                    .name("Demo Viewer")
                    .email("viewer@finance.com")
                    .password(passwordEncoder.encode("Viewer@123"))
                    .role(Role.VIEWER)
                    .active(true)
                    .build());
            log.info("Demo VIEWER user created — email: viewer@finance.com | password: Viewer@123");
        }
    }
}
