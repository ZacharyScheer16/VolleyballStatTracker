package com.zacharyscheer.volleyballstattracker.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StatLineResponseDTO {

    // --- Identifiers ---
    private UUID id;
    private Long setId;
    private Integer playerId;

    // --- Raw Counts ---
    private int attackAttempts;
    private int kills;
    private int killErrors;

    private int threePass;
    private int twoPass;
    private int onePass;
    private int zeroPass;

    private int serviceAttempt;
    private int serviceError;
    private int serviceAce;

    private int digs;
    private int digError;

    private int blocks;
    private int blockError;

    private int setAttempts;
    private int setError;
    private int assists;

    // --- Calculated Metrics ---
    private double hittingPercentage;
    private double passRating;
}