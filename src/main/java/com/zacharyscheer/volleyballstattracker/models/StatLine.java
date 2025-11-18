package com.zacharyscheer.volleyballstattracker.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

// Use specific Lombok annotations for clean code, or use @Data
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatLine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // --- Relationships ---

    // FIX: Change JoinColumn name to conventional 'player_id'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id", nullable = false)
    private Set set;

    // --- Fields ---
    private boolean isStarter = false;

    //Hitting stats
    private int attackAttempts = 0;
    private int kills = 0;
    private int killErrors = 0;

    //Pass Stats
    private int threePass = 0;
    private int twoPass = 0;
    private int onePass = 0;
    private int zeroPass = 0; // Aced / Reception Error

    //Serving Stats
    private int serviceAttempt = 0;
    private int serviceError = 0;
    private int serviceAce = 0;

    //Digging
    private int digs = 0;
    private int digError = 0;

    //Blocking
    private int blocks = 0;
    private int blockError = 0;

    //Setting
    private int setAttempts = 0;
    private int setError = 0;
    private int assists = 0;


    // --- Methods (Correct and kept as is) ---

    public double calculateHittingPercentage() {
        if (this.attackAttempts == 0) {
            return 0.000;
        }
        double percentage = (double) (this.kills - this.killErrors) / this.attackAttempts;
        return Math.round(percentage * 1000.0) / 1000.0;
    }

    public double calculatePassRating(){
        double totalPasses = (double) (this.threePass + this.twoPass + this.onePass + this.zeroPass);
        if (totalPasses == 0) {
            return 0.0;
        }

        double passScore = (double)(this.threePass * 3) + (this.twoPass * 2) +  (this.onePass * 1) + (this.zeroPass * 0);
        double passAverage = passScore / totalPasses;
        return Math.round(passAverage * 100.0) / 100.0;
    }
}