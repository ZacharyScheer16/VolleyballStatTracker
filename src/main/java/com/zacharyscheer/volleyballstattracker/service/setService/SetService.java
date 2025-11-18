package com.zacharyscheer.volleyballstattracker.service.setService;

import com.zacharyscheer.volleyballstattracker.models.Set;

/**
 * Service for managing Set entities, scores, and stat initialization.
 */
public interface SetService {

    /**
     * Finds a set by its ID.
     */
    Set getSetById(Long setId);

    /**
     * Initializes a new set for a match, including creating StatLine entries for all players.
     * @param matchId The ID of the match the set belongs to.
     * @param rosterIds The IDs of the players participating in the set (roster).
     * @return The newly created Set entity.
     */
    Set startNewSet(Long matchId, Iterable<Long> rosterIds);

    /**
     * Records a score point for the home team.
     */
    Set recordHomePoint(Long setId);

    /**
     * Records a score point for the opponent team.
     */
    Set recordOpponentPoint(Long setId);
}