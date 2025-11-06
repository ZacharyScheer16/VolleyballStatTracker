package com.zacharyscheer.volleyballstattracker.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetDTO {
    private Long id;
    private Integer homeScore;
    private Integer awayScore;
    private Integer setNumber;
}
