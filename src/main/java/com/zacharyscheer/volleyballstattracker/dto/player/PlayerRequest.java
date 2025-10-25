package com.zacharyscheer.volleyballstattracker.dto.player;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class PlayerRequest {
    @NotBlank(message = "Player name is required.")
    private String name;

    @NotNull(message = "Jersey number is required.")
    @Positive(message = "Jersey number must be a positive value.")
    private Integer number;

    // Position can be null, but helps with organization
    private String position;
}
