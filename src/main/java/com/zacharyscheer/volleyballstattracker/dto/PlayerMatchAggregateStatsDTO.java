package com.zacharyscheer.volleyballstattracker.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlayerMatchAggregateStatsDTO {
    // Identifiers
    private Long matchId;
    private Integer playerId;

    // --- Raw Counts ---
    // Hitting
    private int kills = 0;
    private int killErrors = 0;
    private int attackAttempts = 0;

    // Passing (Reception)
    private int threePass = 0;
    private int twoPass = 0;
    private int onePass = 0;
    private int zeroPass = 0; // Reception Error (Pass Rating of 0)

    // Serving
    private int serviceAce = 0;
    private int serviceError = 0;
    private int serviceAttempt = 0;

    // Setting
    private int assists = 0;
    private int setError = 0;
    private int setAttempts = 0;

    // Blocking & Defense
    private int blocks = 0;
    private int blockError = 0;
    private int digs = 0;
    private int digError = 0; // Defensive Error

    // --- Calculated Metrics ---
    private double hittingPercentage = 0.000;
    private double passRating = 0.00;
    private double servicePercentage = 0.000;


    /**
     * Calculates all derived metrics after raw counts have been aggregated
     * by the service layer.
     */
    public void calculateMetrics() {
        this.hittingPercentage = this.calculateHittingPercentage();
        this.passRating = this.calculatePassRating();
        this.servicePercentage = this.calculateServicePercentage();
    }

    private double calculateHittingPercentage() {
        if (this.attackAttempts == 0) {
            return 0.000;
        }
        // Formula: (Kills - Errors) / Attempts
        double percentage = (double) (this.kills - this.killErrors) / this.attackAttempts;
        // Round to 3 decimal places
        return Math.round(percentage * 1000.0) / 1000.0;
    }

    private double calculatePassRating() {
        double totalPasses = (double) (this.threePass + this.twoPass + this.onePass + this.zeroPass);

        if (totalPasses == 0) {
            return 0.00;
        }

        // Formula: (3*3Pass + 2*2Pass + 1*1Pass + 0*0Pass) / Total Passes
        double passScore = (double)(this.threePass * 3) + (this.twoPass * 2) +  (this.onePass * 1) + (this.zeroPass * 0);
        double passAverage = passScore / totalPasses;
        // Round to 2 decimal places
        return Math.round(passAverage * 100.0) / 100.0;
    }

    private double calculateServicePercentage() {
        if (this.serviceAttempt == 0) {
            return 0.000;
        }
        // Formula: (Attempts - Errors) / Attempts
        double successfulServes = this.serviceAttempt - this.serviceError;
        double percentage = successfulServes / this.serviceAttempt;
        // Round to 3 decimal places
        return Math.round(percentage * 1000.0) / 1000.0;
    }
}