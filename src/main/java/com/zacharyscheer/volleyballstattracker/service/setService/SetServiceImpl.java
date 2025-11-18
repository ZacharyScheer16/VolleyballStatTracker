package com.zacharyscheer.volleyballstattracker.service.setService;

import com.zacharyscheer.volleyballstattracker.models.*;
import com.zacharyscheer.volleyballstattracker.repository.*;
import com.zacharyscheer.volleyballstattracker.service.setService.SetService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for Set management, focusing on initializing the set data.
 */
@Service
@RequiredArgsConstructor
public class SetServiceImpl implements SetService {

    // Inject all required repositories
    private final SetRepository setRepository;
    private final PlayerRepository playerRepository;
    private final StatLineRepository statLineRepository;
    private final MatchRepository matchRepository;

    // Helper method to retrieve a Set or throw an exception
    private Set findSetOrThrow(Long setId) {
        return setRepository.findById(setId)
                .orElseThrow(() -> new EntityNotFoundException("Set not found with ID: " + setId));
    }

    // --- Core Initialization Method ---

    @Override
    @Transactional
    public Set startNewSet(Long matchId, Iterable<Long> rosterIds) {
        // 1. Find the Match
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match not found with ID: " + matchId));

        // 2. Create and Save the new Set
        Set newSet = new Set();
        newSet.setMatch(match);
        newSet.setHomeScore(0);
        newSet.setOpponentScore(0);

        // -----------------------------------------------------------
        // *** FIX IS HERE ***
        // Calculate the next set number based on the existing sets in the match.
        // Assuming 'match.getSets()' returns the collection of sets for that match.
        int nextSetNumber = match.getSets().size() + 1;
        newSet.setSetNumber(nextSetNumber);
        // -----------------------------------------------------------

        newSet = setRepository.save(newSet); // Save to get the generated Set ID

        // 3. Initialize StatLine for every player on the roster
        for (Long playerId : rosterIds) {
            // Retrieve Player (ensures player exists)
            Player player = playerRepository.findById(Math.toIntExact(playerId))
                    .orElseThrow(() -> new EntityNotFoundException("Player not found with ID: " + playerId));

            // Create and initialize StatLine for this player in this set
            StatLine statLine = new StatLine();
            statLine.setSet(newSet);
            statLine.setPlayer(player);
            // JPA/Hibernate should initialize primitive fields (like kills, blocks, etc.) to 0

            statLineRepository.save(statLine);
        }

        return newSet;
    }

    // --- Score Tracking and Retrieval Methods ---

    @Override
    public Set getSetById(Long setId) {
        return findSetOrThrow(setId);
    }

    @Override
    @Transactional
    public Set recordHomePoint(Long setId) {
        Set set = findSetOrThrow(setId);
        set.setHomeScore(set.getHomeScore() + 1);
        return setRepository.save(set);
    }

    @Override
    @Transactional
    public Set recordOpponentPoint(Long setId) {
        Set set = findSetOrThrow(setId);
        set.setOpponentScore(set.getOpponentScore() + 1);
        return setRepository.save(set);
    }
}