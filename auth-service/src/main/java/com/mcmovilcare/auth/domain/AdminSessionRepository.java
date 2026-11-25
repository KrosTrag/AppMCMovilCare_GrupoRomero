package com.mcmovilcare.auth.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminSessionRepository extends JpaRepository<AdminSession, String> {
}