package com.zacharyscheer.volleyballstattracker.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Match")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Opponent Name
    @Column(nullable = false)
    private String opponentTeam;

    //Date
    @Column(nullable = false)
    private LocalDate matchDate = LocalDate.from(LocalDateTime.now());

    // The opponent's final score in sets (e.g., if we won 3-1, this is 1)
    @Column(nullable = false)
    private Integer opponentSetScore = 0;

    // The user (coach/manager) who logged this match
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Relationship to the sets played in this match
    // CascadeType.ALL ensures that deleting a match deletes all its sets
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Set> sets;

}
