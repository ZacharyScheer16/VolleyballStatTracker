package com.zacharyscheer.volleyballstattracker.repository;

import com.zacharyscheer.volleyballstattracker.models.StatLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatLineRepository extends JpaRepository<StatLine, UUID> {

    /**
     * Finds the unique StatLine for a specific player within a specific set.
     */
    Optional<StatLine> findBySetIdAndPlayerId(Long setId, Integer playerId);
}