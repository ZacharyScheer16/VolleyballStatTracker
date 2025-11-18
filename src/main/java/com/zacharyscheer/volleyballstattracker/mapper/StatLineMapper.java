package com.zacharyscheer.volleyballstattracker.mapper;

import com.zacharyscheer.volleyballstattracker.dto.StatLineResponseDTO;
import com.zacharyscheer.volleyballstattracker.models.StatLine;
import org.springframework.stereotype.Component;

@Component
public class StatLineMapper {

    /**
     * Converts a StatLine JPA entity to a client-friendly StatLineResponseDTO,
     * calculating metrics in the process.
     */
    public StatLineResponseDTO toDto(StatLine entity) {
        // Ensure entity and its relationships are not null before accessing properties
        if (entity == null || entity.getSet() == null || entity.getPlayer() == null) {
            // Handle null case appropriately, maybe return null or throw an exception
            throw new IllegalArgumentException("Cannot map null StatLine or uninitialized relationship.");
        }

        return StatLineResponseDTO.builder()
                .id(entity.getId())
                .setId(entity.getSet().getId())
                .playerId(entity.getPlayer().getId())

                // Raw Counts
                .attackAttempts(entity.getAttackAttempts())
                .kills(entity.getKills())
                .killErrors(entity.getKillErrors())
                .threePass(entity.getThreePass())
                .twoPass(entity.getTwoPass())
                .onePass(entity.getOnePass())
                .zeroPass(entity.getZeroPass())
                .serviceAttempt(entity.getServiceAttempt())
                .serviceError(entity.getServiceError())
                .serviceAce(entity.getServiceAce())
                .digs(entity.getDigs())
                .digError(entity.getDigError())
                .blocks(entity.getBlocks())
                .blockError(entity.getBlockError())
                .setAttempts(entity.getSetAttempts())
                .setError(entity.getSetError())
                .assists(entity.getAssists())

                // Calculated Metrics (using the methods defined in the StatLine entity)
                .hittingPercentage(entity.calculateHittingPercentage())
                .passRating(entity.calculatePassRating())
                .build();
    }
}