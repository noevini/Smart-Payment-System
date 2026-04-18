package com.smartpaymentsystem.repository;

import com.smartpaymentsystem.domain.User;
import com.smartpaymentsystem.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findByBusiness_IdAndRole(Long businessId, UserRole role);
    Optional<User> findByIdAndBusiness_IdAndRole(Long id, Long businessId, UserRole role);
}
