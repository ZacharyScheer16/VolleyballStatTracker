package com.zacharyscheer.volleyballstattracker.models;


import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
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

}
