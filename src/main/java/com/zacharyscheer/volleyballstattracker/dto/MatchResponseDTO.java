package com.zacharyscheer.volleyballstattracker.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponseDTO {
    private Long id;

    private String opponentTeam;

    private LocalDate matchDate;
    // NOTE: This is essential for the UI. Your service must calculate this
    // based on the 'sets' and the opponentSetScore field in your Match entity.
    private Integer homeSetScore;

    private Integer opponentSetScore;

    // ID of the user who recorded the match (avoids sending the full User object)
    private Long userId;

    // The list of individual set details
    private List<SetDTO> sets;
}
