package com.zacharyscheer.volleyballstattracker.service;

import com.zacharyscheer.volleyballstattracker.dto.MatchRequestDTO;
import com.zacharyscheer.volleyballstattracker.dto.MatchResponseDTO;
import com.zacharyscheer.volleyballstattracker.dto.SetDTO;
import com.zacharyscheer.volleyballstattracker.models.Match;
import com.zacharyscheer.volleyballstattracker.models.Set;
import com.zacharyscheer.volleyballstattracker.models.User;
import com.zacharyscheer.volleyballstattracker.repository.MatchRepository;
import com.zacharyscheer.volleyballstattracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public MatchServiceImpl(MatchRepository matchRepository, UserRepository userRepository) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
    }

    //Create match

    @Override
    @Transactional
    public Match createMatch(MatchRequestDTO requestDTO, Integer userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Match match = new Match();
        match.setUser(user);
        match.setOpponentTeam(requestDTO.getOpponentTeam());

        // This variable tracks how many sets the OPPONENT won.
        int opponentSetsWon = 0;
        List<Set> setEntities = new ArrayList<>();

        // Loop using index to generate the set number since it was removed from SetDTO
        List<SetDTO> sets = requestDTO.getSets();
        for(int i = 0; i < sets.size(); i++){
            SetDTO setDTO = sets.get(i);
            Set setEntity = new Set();

            // FIX: setDTO.getSetNumber() is no longer available.
            // We use the loop index (i + 1) to derive the set number.
            setEntity.setSetNumber(i + 1);
            setEntity.setHomeScore(setDTO.getHomeScore());
            setEntity.setOpponentScore(setDTO.getOpponentScore());

            //link back to match
            setEntity.setMatch(match);

            setEntities.add(setEntity);

            // Correct logic to track opponent sets won:
            if (setDTO.getOpponentScore() > setDTO.getHomeScore()){
                opponentSetsWon++;
            }
        }

        match.setOpponentSetScore(opponentSetsWon);

        // **CRITICAL STEP:** Attach the list of Set entities to the Match entity
        match.setSets(setEntities);

        // 5. Save Match (Sets are saved automatically via CascadeType.ALL)
        return matchRepository.save(match);
    }

    @Override
    public List<MatchResponseDTO> getRecentMatchesByUserId(Integer userId) {
        return List.of();
    }

    @Override
    public List<MatchResponseDTO> getAllMatches() {
        return List.of();
    }

    @Override
    public MatchResponseDTO getMatchById(Long matchId) {
        return null;
    }

    @Override
    public MatchResponseDTO updateMatch(Long matchId, MatchRequestDTO requestDTO) {
        return null;
    }

    @Override
    public void deleteMatch(Long matchId) {

    }

}