package com.zacharyscheer.volleyballstattracker.repository;

import com.zacharyscheer.volleyballstattracker.models.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Set entities.
 * Extends JpaRepository to inherit basic CRUD operations.
 */
@Repository
public interface SetRepository extends JpaRepository<Set, Long> {
    // Basic CRUD methods (save, findById, findAll, etc.) are automatically provided by JpaRepository.

    // You can add custom finder methods here if needed, e.g.:
    // List<Set> findAllByMatchId(Long matchId);
}