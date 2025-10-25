package com.zacharyscheer.volleyballstattracker.service;

import com.zacharyscheer.volleyballstattracker.dto.player.PlayerRequest;
import com.zacharyscheer.volleyballstattracker.models.Player;
import com.zacharyscheer.volleyballstattracker.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * Retrieves all players on the roster, sorted by number (Roster view).
     */
    public List<Player> findAllPlayers() {
        return playerRepository.findAllByOrderByNumberAsc();
    }
    /**
     * Finding by JERSEY NUMBER
     */
    public Optional<Player> findByNumber(Integer number) {
        return playerRepository.findByNumber(number);
    }

    /**
     * Retrieves a player by their ID.
     */
    public Optional<Player> findById(Integer id) {
        return playerRepository.findById(id);
    }

    /**
     * Creates a new player, enforcing the unique jersey number rule.
     */
    public Player createPlayer(PlayerRequest request) {
        if(playerRepository.existsByNumber(request.getNumber())){
            throw new IllegalArgumentException("Jersey number already taken");
        }
        //map to Dto
        Player  player = Player.builder()
                .name(request.getName())
                .number(request.getNumber())
                .position(request.getPosition())
                .build();
        return playerRepository.save(player);
    }

    public Player updatePlayer(Integer id, PlayerRequest request) {
        Player existingPlayer = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with ID: " + id));

        // Check for number conflict only if the number is actually being changed
        if (!existingPlayer.getNumber().equals(request.getNumber())) {
            if (playerRepository.existsByNumber(request.getNumber())) {
                // Check if the conflicting number belongs to a *different* player
                Optional<Player> conflictingPlayer = playerRepository.findByNumber(request.getNumber());
                if (conflictingPlayer.isPresent() && !conflictingPlayer.get().getId().equals(id)) {
                    throw new IllegalArgumentException("Jersey number " + request.getNumber() + " is already in use by another player.");
                }
            }
        }

        // Apply updates
        existingPlayer.setName(request.getName());
        existingPlayer.setNumber(request.getNumber());
        existingPlayer.setPosition(request.getPosition()); // Position update

        return playerRepository.save(existingPlayer);
    }

    /**
     *
     * @param id
     * Deleting Player
     */

    public void deletePlayer(Integer id) {
        if(!playerRepository.existsById(id)){
            throw new IllegalArgumentException("Player not found");
        }
        playerRepository.deleteById(id);
    }
}
