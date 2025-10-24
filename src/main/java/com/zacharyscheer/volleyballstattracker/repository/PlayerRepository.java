package com.zacharyscheer.volleyballstattracker.repository;

import com.zacharyscheer.volleyballstattracker.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Integer> {

    /**
     * Checks if a player with a given jersey number exists.
     * Used for validation to prevent duplicate numbers during creation.
     */
    Optional<Player> findByNumber(Integer number);

    /**
     * Checks if a player with a given jersey number exists.
     * Used for validation to prevent duplicate numbers during creation.
     */
    boolean existsByNumber(Integer number);

    /**
     * Retrieves all players, sorted by jersey number in ascending order for a clean roster view.
     */
    List<Player> findAllByOrderByNumberAsc();

}
