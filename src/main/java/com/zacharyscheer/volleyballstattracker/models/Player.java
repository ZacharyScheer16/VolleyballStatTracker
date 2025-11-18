package com.zacharyscheer.volleyballstattracker.models;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "Players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private Integer number; // Jersey number, assumed to be unique

    @Column
    private String position; // e.g., "Outside Hitter", "Libero", "Setter"

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<StatLine> statLines;

    // --- Relationship to User/Team Owner ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user; // Links this player to the team's owner/manager

}
