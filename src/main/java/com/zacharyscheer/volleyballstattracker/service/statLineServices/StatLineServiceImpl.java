package com.zacharyscheer.volleyballstattracker.service.statLineServices;

import com.zacharyscheer.volleyballstattracker.models.StatLine;
import com.zacharyscheer.volleyballstattracker.repository.StatLineRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // FIX: Directly increment attack attempts here for efficiency
        sl.setAttackAttempts(sl.getAttackAttempts() + 1);

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
        line.setAttackAttempts(line.getAttackAttempts() +1);
        return statLineRepository.save(line);
    }

    // ----------------------------------------------------------------------------------
    // REST OF STATS (Returning null/Not implemented yet)
    // ----------------------------------------------------------------------------------

    @Override
    public StatLine recordServiceAce(Long setId, Integer playerId) {
        StatLine line =  findStatLine(setId, playerId);
        line.setServiceAce(line.getServiceAce() + 1);
        line.setServiceAttempt(line.getServiceAttempt() + 1);

        return statLineRepository.save(line);
    }

    @Override
    public StatLine recordServiceAttempt(Long setId, Integer playerId) {
        StatLine line = findStatLine(setId, playerId);
        line.setServiceAttempt(line.getServiceAttempt() + 1);
        return statLineRepository.save(line);
    }

    @Override
    public StatLine recordServiceError(Long setId, Integer playerId) {
        StatLine line = findStatLine(setId, playerId);
        line.setServiceAttempt(line.getServiceAttempt() + 1);
        line.setServiceError(line.getServiceError() + 1);
        return statLineRepository.save(line);
    }

    @Override
    public StatLine recordPassRating(Long setId, Integer playerId, int rating) {
        StatLine line = findStatLine(setId, playerId);
        if(rating <0 || rating >3){
            throw new IllegalArgumentException("rating should be between 0 and 3");
        }else if(rating == 0){
            line.setZeroPass(line.getZeroPass() + 1);
        }else if(rating == 1){
            line.setOnePass(line.getOnePass() + 1);
        }else if(rating == 2){
            line.setTwoPass(line.getTwoPass() + 1);
        }else {
            line.setThreePass(line.getThreePass() + 1);
        }
        StatLine saved =  statLineRepository.save(line);


        return saved;
    }

    @Override
    public StatLine recordDig(Long setId, Integer playerId) {
        StatLine line = findStatLine(setId, playerId);
        line.setDigs(line.getDigs() + 1);
        return statLineRepository.save(line);
    }

    @Override
    public StatLine recordDigError(Long setId, Integer playerId) {
        StatLine line = findStatLine(setId, playerId);
        line.setDigError(line.getDigError() + 1);
        return statLineRepository.save(line);
    }

    @Override
    public StatLine recordBlock(Long setId, Integer playerId) {
        return null;
    }

    @Override
    public StatLine recordBlockError(Long setId, Integer playerId) {
        return null;
    }

    @Override
    public StatLine recordAssist(Long setId, Integer playerId) {
        return null;
    }

    @Override
    public StatLine recordSetAttempt(Long setId, Integer playerId) {
        return null;
    }

    @Override
    public StatLine recordSetError(Long setId, Integer playerId) {
        return null;
    }
}