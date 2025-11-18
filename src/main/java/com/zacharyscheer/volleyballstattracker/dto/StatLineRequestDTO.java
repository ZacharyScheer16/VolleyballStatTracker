package com.zacharyscheer.volleyballstattracker.dto.stat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatLineRequestDTO {

    // --- Required Identifiers ---

    @NotNull(message = "Set ID is required.")
    private Long setId;

    @NotNull(message = "Player ID is required.")
    private Integer playerId;

    // --- Stat Counts (Representing the amount to record/update) ---

    // Hitting
    @Min(0) private int kills = 0;
    @Min(0) private int killErrors = 0;
    @Min(0) private int attackAttempts = 0;

    // Passing (Note: This is difficult for a simple bulk entry DTO.
    // For bulk entry, you usually submit the total count for each category.)
    @Min(0) private int threePass = 0;
    @Min(0) private int twoPass = 0;
    @Min(0) private int onePass = 0;
    @Min(0) private int zeroPass = 0;

    // Serving
    @Min(0) private int serviceAce = 0;
    @Min(0) private int serviceError = 0;
    @Min(0) private int serviceAttempt = 0;

    // Digging
    @Min(0) private int digs = 0;
    @Min(0) private int digError = 0;

    // Blocking
    @Min(0) private int blocks = 0;
    @Min(0) private int blockError = 0;

    // Setting
    @Min(0) private int assists = 0;
    @Min(0) private int setError = 0;
    @Min(0) private int setAttempts = 0;
}