package com.zacharyscheer.volleyballstattracker.controller;


import com.zacharyscheer.volleyballstattracker.dto.player.PlayerRequest;
import com.zacharyscheer.volleyballstattracker.models.Player;
import com.zacharyscheer.volleyballstattracker.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor // required args constructor implements constructor
public class PlayerController {
    private final PlayerService playerService;

    // ---- Get (READ ALL PLAYERS) ---//
    /**
     * GET /api/players : Retrieves the entire roster.
     */
    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        List<Player> players = playerService.findAllPlayers();
        return ResponseEntity.ok(players);
    }

    // ---- Get (READ One PLAYERS) ---//
    /**
     * GET /api/players/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Integer id) {
        return playerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- 3. POST (Create) ---
    /**
     * POST /api/players : Adds a new player to the roster.
     */
    @PostMapping
    public ResponseEntity<?> addPlayer(@Valid @RequestBody PlayerRequest request) {
        try {
            // @Valid triggers DTO validation (@NotNull, @NotBlank, etc.)
            Player newPlayer = playerService.createPlayer(request);
            // Returns 201 Created and the new player object
            return ResponseEntity.status(HttpStatus.CREATED).body(newPlayer);
        } catch (IllegalArgumentException e) {
            // Handles business logic exceptions from the service (e.g., duplicate jersey number)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // --- 4. PUT (Update) ---
    /**
     * PUT /api/players/{id} : Updates an existing player's details.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlayer(@PathVariable Integer id, @Valid @RequestBody PlayerRequest request) {
        try {
            Player updatedPlayer = playerService.updatePlayer(id, request);
            return ResponseEntity.ok(updatedPlayer);
        } catch (IllegalArgumentException e) {
            // Handles "Player not found" or "Jersey number conflict"
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // --- 5. DELETE (Delete) ---
    /**
     * DELETE /api/players/{id} : Removes a player from the roster.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlayer(@PathVariable Integer id) {
        try {
            playerService.deletePlayer(id);
            // Returns 200 OK
            return ResponseEntity.ok("Player deleted successfully.");
        } catch (IllegalArgumentException e) {
            // Handles "Player not found"
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Retrieves all players on the roster, sorted by number (Roster view).
     */
    @GetMapping("/number/{number}")
    public ResponseEntity<Player> getPlayersByNumber(@PathVariable Integer number) {
        return playerService.findByNumber(number)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
