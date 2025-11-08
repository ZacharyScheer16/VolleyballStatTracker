package com.zacharyscheer.volleyballstattracker.service;

import com.zacharyscheer.volleyballstattracker.dto.MatchRequestDTO;
import com.zacharyscheer.volleyballstattracker.dto.MatchResponseDTO;
import com.zacharyscheer.volleyballstattracker.models.Match;
import java.util.List;

public interface MatchService {

    // --- C: Create ---
    Match createMatch(MatchRequestDTO matchRequestDTO, Integer userId);

    // --- R: Read ---
    List<MatchResponseDTO> getRecentMatchesByUserId(Integer userId);
    List<MatchResponseDTO> getAllMatches();
    MatchResponseDTO getMatchById(Long matchId);

    // --- U: Update ---
    MatchResponseDTO updateMatch(Long matchId, MatchRequestDTO requestDTO);

    // --- D: Delete ---
    void deleteMatch(Long matchId);
}