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
import java.util.stream.Collectors;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public MatchServiceImpl(MatchRepository matchRepository, UserRepository userRepository) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
    }

    // --- C: Create Match ---

    @Override
    @Transactional
    public Match createMatch(MatchRequestDTO requestDTO, Integer userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Match match = new Match();
        match.setUser(user);
        match.setOpponentTeam(requestDTO.getOpponentTeam());

        int homeSetsWon = 0;
        int opponentSetsWon = 0;
        List<Set> setEntities = new ArrayList<>();

        List<SetDTO> sets = requestDTO.getSets();
        for(int i = 0; i < sets.size(); i++){
            SetDTO setDTO = sets.get(i);
            Set setEntity = new Set();

            // Set number derived from loop index
            setEntity.setSetNumber(i + 1);
            setEntity.setHomeScore(setDTO.getHomeScore());
            setEntity.setOpponentScore(setDTO.getOpponentScore());
            setEntity.setMatch(match);
            setEntities.add(setEntity);

            // Logic to track sets won:
            if (setDTO.getHomeScore() > setDTO.getOpponentScore()){
                homeSetsWon++;
            } else if (setDTO.getOpponentScore() > setDTO.getHomeScore()){
                opponentSetsWon++;
            }
        }

        // Set both home and opponent scores on the Match entity
        match.setHomeSetScore(homeSetsWon);
        match.setOpponentSetScore(opponentSetsWon);

        match.setSets(setEntities);

        return matchRepository.save(match);
    }

    // --- R: Read Match ---

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponseDTO> getRecentMatchesByUserId(Integer userId) {
        // 1. Check if the user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }

        // 2. Fetch the top 10 most recent matches for this user
        List<Match> recentMatches = matchRepository.findTop10ByUser_IdOrderByMatchDateDesc(userId);

        // 3. Map the list of entities to a list of DTOs
        return recentMatches.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponseDTO> getAllMatches() {
        return matchRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MatchResponseDTO getMatchById(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match not found with id: " + matchId));
        return mapToResponseDTO(match);
    }

    // --- U/D: Update/Delete Placeholders ---

    @Override
    public MatchResponseDTO updateMatch(Long matchId, MatchRequestDTO requestDTO) {
        // Implementation pending
        return null;
    }

    @Override
    public void deleteMatch(Long matchId) {
        // Implementation pending
    }


    // --- Mapping Utilities ---

    /**
     * Converts a Match JPA Entity into a MatchResponseDTO.
     */
    private MatchResponseDTO mapToResponseDTO(Match match) {
        MatchResponseDTO dto = new MatchResponseDTO();

        dto.setId(match.getId());
        dto.setOpponentTeam(match.getOpponentTeam());
        dto.setMatchDate(match.getMatchDate());
        dto.setHomeSetScore(match.getHomeSetScore());
        dto.setOpponentSetScore(match.getOpponentSetScore());
        dto.setUserId(Long.valueOf(match.getUser().getId()));

        List<SetDTO> setDTOs = match.getSets().stream()
                .map(this::mapSetToDTO)
                .collect(Collectors.toList());
        dto.setSets(setDTOs);

        return dto;
    }

    /**
     * Converts a Set JPA Entity into a SetDTO.
     */
    private SetDTO mapSetToDTO(Set set) {
        SetDTO dto = new SetDTO();
        dto.setId(set.getId());
        dto.setHomeScore(set.getHomeScore());
        dto.setOpponentScore(set.getOpponentScore());
        return dto;
    }

}
