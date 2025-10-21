package com.zacharyscheer.volleyballstattracker.repository;

import com.zacharyscheer.volleyballstattracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // <-- NEW IMPORT
import org.springframework.data.jpa.repository.Query;      // <-- NEW IMPORT
import org.springframework.data.repository.query.Param;    // <-- NEW IMPORT (Best practice for named parameters)
import org.springframework.transaction.annotation.Transactional; // <-- NEW IMPORT

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    // 🛑 THIS IS THE REQUIRED FIX 🛑
    @Modifying // MANDATORY: Flags this as a modifying (UPDATE/DELETE) query.
    @Transactional // MANDATORY: Ensures the update runs within a database transaction.
    @Query("UPDATE User u SET u.email = :newEmail WHERE u.email = :oldEmail") // MANDATORY: Provides the actual JPQL query.
    // NOTE: Changed return type to 'int' (standard for bulk updates) and used named parameters.
    int updateEmail(@Param("oldEmail") String oldEmail, @Param("newEmail") String newEmail);

    // You will need to change the method call in your service layer to handle the 'int' return type.
}