package com.zacharyscheer.volleyballstattracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO used to receive match data from the frontend when creating a new match (POST).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequestDTO {

    // Maps to Match.opponentTeam
    private String opponentTeam;

    // The list of completed sets (using the shared SetDTO)
    private List<SetDTO> sets;

}