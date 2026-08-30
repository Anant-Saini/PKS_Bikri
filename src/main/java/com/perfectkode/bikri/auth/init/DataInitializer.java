package com.perfectkode.bikri.auth.init;

import com.perfectkode.bikri.auth.model.Role;
import com.perfectkode.bikri.auth.model.User;
import com.perfectkode.bikri.auth.repository.RoleRepository;
import com.perfectkode.bikri.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Seed Roles
        Role userRole = createRoleIfNotFound("ROLE_USER", 1001);
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", 1002);

        // 2. Seed Default Admin User
        createAdminIfNotFound("admin@ecommerce.com", "Admin@123", adminRole);
    }

    private Role createRoleIfNotFound(String roleName, Integer roleCode) {
        return roleRepository.findByRoleCode(roleCode)
                .orElseGet(() -> roleRepository.save(new Role(null, roleName, roleCode)));
    }

    private void createAdminIfNotFound(String email, String rawPassword, Role adminRole) {
        if (!userRepository.existsByEmail(email)) {
            User admin = new User(
                    null,
                    email,
                    passwordEncoder.encode(rawPassword), // Hash password using BCrypt
                    true,
                    true,
                    Instant.now(),
                    adminRole
            );
            admin.setVerified(true);
            userRepository.save(admin);
        }
    }
}
