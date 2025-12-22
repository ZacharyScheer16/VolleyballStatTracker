package com.zacharyscheer.volleyballstattracker.service.statLineServices;


import com.zacharyscheer.volleyballstattracker.dto.PlayerMatchAggregateStatsDTO;
import com.zacharyscheer.volleyballstattracker.dto.TeamMatchAggregateStatsDTO;
import com.zacharyscheer.volleyballstattracker.models.StatLine;
import com.zacharyscheer.volleyballstattracker.repository.StatLineRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StatLineServiceImpl implements StatLineService {
    private final StatLineRepository statLineRepository;

    public StatLineServiceImpl(StatLineRepository statLineRepository) {
        this.statLineRepository = statLineRepository;
    }

    /**
     * Core utility to fetch the unique StatLine for a player within a set.
     */
    private StatLine findStatLine(Long setId, Integer playerId) {
        // This relies on the custom repository method we defined previously.
        return statLineRepository.findBySetIdAndPlayerId(setId, playerId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("StatLine not found for Set ID %d and Player ID %d. Ensure the set was started correctly.", setId, playerId)
                ));
    }

    // --- Aggregation Utility (Generic Helper Method) ---
    /**
     * Internal generic method to sum all raw stats from a list of StatLine entities
     * into an aggregation DTO (either Player or Team).
     * @param statLines The list of StatLine entities to aggregate.
     * @param totalsDTO The DTO object (PlayerMatchAggregateStatsDTO or TeamMatchAggregateStatsDTO)
     * to store the aggregated results.
     * @param <T> A type that extends PlayerMatchAggregateStatsDTO.
     * @return The populated totalsDTO with calculated metrics.
     */
    private <T extends PlayerMatchAggregateStatsDTO> T aggregateRawStats(List<StatLine> statLines, T totalsDTO) {
        if (statLines.isEmpty()) {
            // No data, but we still need to calculate metrics which will all be 0.0
            totalsDTO.calculateMetrics();
            return totalsDTO;
        }

        statLines.forEach(statLine -> {
            // Hitting
            totalsDTO.setKills(totalsDTO.getKills() + statLine.getKills());
            totalsDTO.setKillErrors(totalsDTO.getKillErrors() + statLine.getKillErrors());
            totalsDTO.setAttackAttempts(totalsDTO.getAttackAttempts() + statLine.getAttackAttempts());

            // Passing
            totalsDTO.setThreePass(totalsDTO.getThreePass() + statLine.getThreePass());
            totalsDTO.setTwoPass(totalsDTO.getTwoPass() + statLine.getTwoPass());
            totalsDTO.setOnePass(totalsDTO.getOnePass() + statLine.getOnePass());
            totalsDTO.setZeroPass(totalsDTO.getZeroPass() + statLine.getZeroPass());

            // Serving
            totalsDTO.setServiceAce(totalsDTO.getServiceAce() + statLine.getServiceAce());
            totalsDTO.setServiceError(totalsDTO.getServiceError() + statLine.getServiceError());
            totalsDTO.setServiceAttempt(totalsDTO.getServiceAttempt() + statLine.getServiceAttempt());

            // Setting
            totalsDTO.setAssists(totalsDTO.getAssists() + statLine.getAssists());
            totalsDTO.setSetError(totalsDTO.getSetError() + statLine.getSetError());
            totalsDTO.setSetAttempts(totalsDTO.getSetAttempts() + statLine.getSetAttempts());

            // Blocking & Defense
            totalsDTO.setBlocks(totalsDTO.getBlocks() + statLine.getBlocks());
            totalsDTO.setBlockError(totalsDTO.getBlockError() + statLine.getBlockError());
            totalsDTO.setDigs(totalsDTO.getDigs() + statLine.getDigs());
            totalsDTO.setDigError(totalsDTO.getDigError() + statLine.getDigError());
        });

        // Calculate metrics like percentages after all raw stats are summed up
        totalsDTO.calculateMetrics();
        return totalsDTO;
    }

    // ----------------------------------------------------------------------------------
    // AGGREGATION METHODS (Match Totals)
    // ----------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public PlayerMatchAggregateStatsDTO getIndividualMatchTotals(Long matchId, Integer playerId) {
        // NOTE: This repository method must be defined in StatLineRepository.java
        List<StatLine> statLines = statLineRepository.findByPlayerIdAndSetMatchId(playerId, matchId);

        PlayerMatchAggregateStatsDTO totals = new PlayerMatchAggregateStatsDTO();
        totals.setPlayerId(playerId);
        totals.setMatchId(matchId);

        // Use the generic helper to aggregate the raw stats and calculate metrics
        return aggregateRawStats(statLines, totals);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamMatchAggregateStatsDTO getTeamMatchTotals(Long matchId) {
        // NOTE: This repository method must be defined in StatLineRepository.java
        List<StatLine> statLines = statLineRepository.findBySetMatchId(matchId);

        TeamMatchAggregateStatsDTO totals = new TeamMatchAggregateStatsDTO();
        totals.setMatchId(matchId);

        // Use the generic helper to aggregate the raw stats and calculate metrics
        return aggregateRawStats(statLines, totals);
    }

    // ----------------------------------------------------------------------------------
    // SINGLE SET STAT RETRIEVAL
    // ----------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public StatLine getStatLineBySetAndPlayer(Long setId, Integer playerId) {
        return findStatLine(setId, playerId);
    }

    // ----------------------------------------------------------------------------------
    // HITTING STATS
    // ----------------------------------------------------------------------------------

    @Override
    @Transactional
    public StatLine recordKill(Long setId, Integer playerId) {
        StatLine sl = findStatLine(setId, playerId);
        sl.setKills(sl.getKills() + 1);
        sl.setAttackAttempts(sl.getAttackAttempts() + 1); // Kill is always an attempt

        return statLineRepository.save(sl);
    }

    @Override
    @Transactional
    public StatLine recordAttackAttempt(Long setId, Integer playerId) {
        StatLine line = findStatLine(setId, playerId);
        line.setAttackAttempts(line.getAttackAttempts() + 1);
        return statLineRepository.save(line);
    }

    @Transactional
    @Override
    public StatLine recordKillError(Long setId, Integer playerId) {
        StatLine line = findStatLine(setId, playerId);
        line.setKillErrors(line.getKillErrors() + 1);
        line.setAttackAttempts(line.getAttackAttempts() + 1); // Kill error is always an attempt
        return statLineRepository.save(line);
    }

    // ----------------------------------------------------------------------------------
    // SERVING STATS
    // ----------------------------------------------------------------------------------

    @Override
    @Transactional
    public StatLine recordServiceAce(Long setId, Integer playerId) {
        StatLine line =  findStatLine(setId, playerId);
        line.setServiceAce(line.getServiceAce() + 1);
        line.setServiceAttempt(line.getServiceAttempt() + 1); // Ace is always an attempt

        return statLineRepository.save(line);
    }

    @Override
    @Transactional
    public StatLine recordServiceAttempt(Long setId, Integer playerId) {
        StatLine line = findStatLine(setId, playerId);
        line.setServiceAttempt(line.getServiceAttempt() + 1);
        return statLineRepository.save(line);
    }

    @Override
    @Transactional
    public StatLine recordServiceError(Long setId, Integer playerId) {
        StatLine line = findStatLine(setId, playerId);
        line.setServiceAttempt(line.getServiceAttempt() + 1); // Error is always an attempt
        line.setServiceError(line.getServiceError() + 1);
        return statLineRepository.save(line);
    }

    // ----------------------------------------------------------------------------------
    // PASSING (RECEPTION) STATS
    // ----------------------------------------------------------------------------------

    @Override
    @Transactional
    public StatLine recordPassRating(Long setId, Integer playerId, int rating) {
        StatLine line = findStatLine(setId, playerId);

        if(rating < 0 || rating > 3){
            throw new IllegalArgumentException("rating should be between 0 and 3");
        }

        // Use a switch statement for cleaner logic flow
        switch (rating) {
            case 3:
                line.setThreePass(line.getThreePass() + 1);
                break;
            case 2:
                line.setTwoPass(line.getTwoPass() + 1);
                break;
            case 1:
                line.setOnePass(line.getOnePass() + 1);
                break;
            case 0:
                line.setZeroPass(line.getZeroPass() + 1);
                break;
        }

        return statLineRepository.save(line);
    }

    // ----------------------------------------------------------------------------------
    // DEFENSE & BLOCKING STATS
    // ----------------------------------------------------------------------------------

    @Override
    @Transactional
    public StatLine recordDig(Long setId, Integer playerId) {
        StatLine line = findStatLine(setId, playerId);
        line.setDigs(line.getDigs() + 1);
        return statLineRepository.save(line);
    }

    @Override
    @Transactional
    public StatLine recordDigError(Long setId, Integer playerId) {
        StatLine line = findStatLine(setId, playerId);
        line.setDigError(line.getDigError() + 1);
        return statLineRepository.save(line);
    }

    @Override
    @Transactional
    public StatLine recordBlock(Long setId, Integer playerId) {
        StatLine line = findStatLine(setId, playerId);
        line.setBlocks(line.getBlocks() + 1);
        return statLineRepository.save(line);
    }

    @Override
    @Transactional
    public StatLine recordBlockError(Long setId, Integer playerId) {
        StatLine line = findStatLine(setId, playerId);
        line.setBlockError(line.getBlockError() + 1);
        return statLineRepository.save(line);
    }

    // ----------------------------------------------------------------------------------
    // SETTING STATS
    // ----------------------------------------------------------------------------------

    @Override
    @Transactional
    public StatLine recordAssist(Long setId, Integer playerId) {
        StatLine line = findStatLine(setId, playerId);
        line.setAssists(line.getAssists() + 1);
        line.setSetAttempts(line.getSetAttempts() + 1); // FIX 3: An assist is also a set attempt
        return statLineRepository.save(line);
    }

    @Override
    @Transactional
    public StatLine recordSetAttempt(Long setId, Integer playerId) {
        StatLine line = findStatLine(setId, playerId);
        line.setSetAttempts(line.getSetAttempts() + 1);
        return statLineRepository.save(line);
    }

    @Override
    @Transactional
    public StatLine recordSetError(Long setId, Integer playerId) {
        StatLine line = findStatLine(setId, playerId);
        line.setSetError(line.getSetError() + 1);
        line.setSetAttempts(line.getSetAttempts() + 1); // FIX 4: A set error is also a set attempt
        return statLineRepository.save(line);
    }
}