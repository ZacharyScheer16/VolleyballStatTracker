package com.zacharyscheer.volleyballstattracker.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the entire team's aggregated stats across a match.
 * It extends PlayerMatchAggregateStatsDTO to inherit all stat fields and calculation logic,
 * but explicitly sets playerId to null as it represents the team total.
 */
@Data
public class TeamMatchAggregateStatsDTO extends PlayerMatchAggregateStatsDTO {

    // Redefine constructor to ensure proper initialization
    public TeamMatchAggregateStatsDTO() {
        super();
        // The PlayerMatchAggregateStatsDTO contains matchId.
        // We ensure playerId is null since this DTO represents the team, not an individual.
        super.setPlayerId(null);
    }
}