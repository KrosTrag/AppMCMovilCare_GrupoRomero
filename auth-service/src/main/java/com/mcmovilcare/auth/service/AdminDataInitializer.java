package com.mcmovilcare.auth.service;

import com.mcmovilcare.auth.domain.AdminUser;
import com.mcmovilcare.auth.domain.AdminUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

@Configuration
public class AdminDataInitializer {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner inicializarAdministrador(AdminUserRepository userRepository,
                                                PasswordEncoder passwordEncoder,
                                                @Value("${app.admin.email}") String email,
                                                @Value("${app.admin.password}") String password) {
        return args -> {
            String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
            if (!userRepository.existsById(normalizedEmail)) {
                userRepository.save(new AdminUser(normalizedEmail, passwordEncoder.encode(password)));
            }
        };
    }
}