package com.zacharyscheer.volleyballstattracker.service.statLineServices;

import com.zacharyscheer.volleyballstattracker.models.StatLine;
import java.util.UUID;

public interface StatLineService {

    // --- Utility Finders (for aggregation) ---

    /**
     * Finds the StatLine for a specific player in a specific set.
     */
    StatLine getStatLineBySetAndPlayer(Long setId, Integer playerId);

    // --- Hitting ---
    StatLine recordKill(Long setId, Integer playerId);
    StatLine recordAttackAttempt(Long setId, Integer playerId); // Used when only an attempt is recorded (e.g., hit covered)
    StatLine recordKillError(Long setId, Integer playerId);

    // --- Serving ---
    StatLine recordServiceAce(Long setId, Integer playerId);
    StatLine recordServiceAttempt(Long setId, Integer playerId);
    StatLine recordServiceError(Long setId, Integer playerId);

    // --- Passing (Reception) ---
    StatLine recordPassRating(Long setId, Integer playerId, int rating); // Rating: 3, 2, 1, or 0 (Ace)

    // --- Digging / Defense ---
    StatLine recordDig(Long setId, Integer playerId);
    StatLine recordDigError(Long setId, Integer playerId);

    // --- Blocking ---
    StatLine recordBlock(Long setId, Integer playerId);
    StatLine recordBlockError(Long setId, Integer playerId);

    // --- Setting ---
    StatLine recordAssist(Long setId, Integer playerId);
    StatLine recordSetAttempt(Long setId, Integer playerId);
    StatLine recordSetError(Long setId, Integer playerId);
}