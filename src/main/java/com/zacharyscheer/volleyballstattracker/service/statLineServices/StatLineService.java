package com.zacharyscheer.volleyballstattracker.service.statLineServices;

import com.zacharyscheer.volleyballstattracker.dto.PlayerMatchAggregateStatsDTO;
import com.zacharyscheer.volleyballstattracker.dto.TeamMatchAggregateStatsDTO;
import com.zacharyscheer.volleyballstattracker.models.StatLine;

public interface StatLineService {

    // --- Utility Finders ---

    /**
     * Finds the StatLine for a specific player in a specific set.
     */
    StatLine getStatLineBySetAndPlayer(Long setId, Integer playerId);

    // --- Aggregation Methods (NEW) ---

    /**
     * Aggregates ALL stats for a single player across ALL sets in a match and calculates metrics.
     * @param matchId The ID of the match to aggregate stats for.
     * @param playerId The ID of the player.
     * @return The aggregated match totals for the individual player.
     */
    PlayerMatchAggregateStatsDTO getIndividualMatchTotals(Long matchId, Integer playerId);

    /**
     * Aggregates ALL stats for the entire team (all players) across ALL sets in a match and calculates metrics.
     * @param matchId The ID of the match to aggregate stats for.
     * @return The aggregated match totals for the entire team.
     */
    TeamMatchAggregateStatsDTO getTeamMatchTotals(Long matchId);

    // --- Hitting Actions ---
    StatLine recordKill(Long setId, Integer playerId);
    StatLine recordAttackAttempt(Long setId, Integer playerId);
    StatLine recordKillError(Long setId, Integer playerId);

    // --- Serving Actions ---
    StatLine recordServiceAce(Long setId, Integer playerId);
    StatLine recordServiceAttempt(Long setId, Integer playerId);
    StatLine recordServiceError(Long setId, Integer playerId);

    // --- Passing (Reception) Actions ---
    StatLine recordPassRating(Long setId, Integer playerId, int rating);

    // --- Digging / Defense Actions ---
    StatLine recordDig(Long setId, Integer playerId);
    StatLine recordDigError(Long setId, Integer playerId);

    // --- Blocking Actions ---
    StatLine recordBlock(Long setId, Integer playerId);
    StatLine recordBlockError(Long setId, Integer playerId);

    // --- Setting Actions ---
    StatLine recordAssist(Long setId, Integer playerId);
    StatLine recordSetAttempt(Long setId, Integer playerId);
    StatLine recordSetError(Long setId, Integer playerId);
}