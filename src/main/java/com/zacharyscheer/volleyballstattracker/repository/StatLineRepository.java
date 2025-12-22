package com.zacharyscheer.volleyballstattracker.repository;

import com.zacharyscheer.volleyballstattracker.models.StatLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatLineRepository extends JpaRepository<StatLine, UUID> {

    /**
     * Finds the unique StatLine for a specific player within a specific set.
     */
    Optional<StatLine> findBySetIdAndPlayerId(Long setId, Integer playerId);

    /**
     * Finds all StatLines for a specific player across all sets of a match.
     * This method must return a List<StatLine> for match aggregation.
     */
    List<StatLine> findByPlayerIdAndSetMatchId(Integer playerId, Long matchId);

    /**
     * Finds all StatLines for all players in a specific match (used for team totals).
     */
    List<StatLine> findBySetMatchId(Long matchId);
}