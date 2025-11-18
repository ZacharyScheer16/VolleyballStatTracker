package com.zacharyscheer.volleyballstattracker.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sets")
public class Set {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The order of the set (1, 2, 3, 4, or 5)
    @Column(nullable = false)
    private Integer setNumber;

    // Our team's final score for this set
    @Column(nullable = false)
    private Integer homeScore = 0;

    // The opponent's final score for this set
    @Column(nullable = false)
    private Integer opponentScore = 0;

    // Many-to-one relationship back to the Match
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    @JsonIgnore
    private Match match;

    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StatLine> playerStats; // List of StatLines for this set

    // --- Getters and Setters (omitted for brevity) ---
    // ...
}