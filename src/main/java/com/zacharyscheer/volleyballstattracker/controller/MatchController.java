package com.zacharyscheer.volleyballstattracker.controller;

import com.zacharyscheer.volleyballstattracker.dto.MatchRequestDTO;
import com.zacharyscheer.volleyballstattracker.dto.MatchResponseDTO;
import com.zacharyscheer.volleyballstattracker.models.Match;
import com.zacharyscheer.volleyballstattracker.service.MatchService;
import com.zacharyscheer.volleyballstattracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors; // Necessary if you implement set mapping later

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MatchController {

    private final MatchService matchService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<MatchResponseDTO> createMatch(@RequestBody MatchRequestDTO requestDTO,
                                                        Authentication authentication) {

        // 1. Get the authenticated user's database ID
        Integer userId = getUserId(authentication);

        // 2. Create and save the match (newMatch now has the generated ID and saved data)
        Match newMatch = matchService.createMatch(requestDTO, userId);

        // 3. Construct the response by mapping the saved entity
        MatchResponseDTO responseDTO = mapToResponseDTO(newMatch);

        // Use created status (201) and set the location header
        return ResponseEntity
                .created(URI.create("/api/matches/" + newMatch.getId()))
                .body(responseDTO);
    }

    /**
     * Extracts the database ID from the authenticated user principal.
     */
    private Integer getUserId(Authentication authentication) {
        // Get the username/email from the Spring Security context
        String userEmail = authentication.getName();
        // Use the UserService to find the User object and retrieve the actual ID
        return userService.findByEmail(userEmail).getId();
    }

    /**
     * Converts the Match JPA Entity into the MatchResponseDTO for the client.
     */
    private MatchResponseDTO mapToResponseDTO(Match match) {
        MatchResponseDTO dto = new MatchResponseDTO();

        // Map top-level Match properties
        dto.setId(match.getId());
        dto.setOpponentTeam(match.getOpponentTeam());
        dto.setMatchDate(match.getMatchDate());

        // Map calculated scores
        // NOTE: This assumes MatchResponseDTO has a setHomeSetScore() method.
        // Since homeSetScore logic is missing in the service, we temporarily set it to 0.
        // You should calculate this in MatchServiceImpl.
        dto.setHomeSetScore(0);
        dto.setOpponentSetScore(match.getOpponentSetScore());

        // Map the User ID. User ID from the User entity is typically Integer,
        // so we convert it if the DTO expects a Long.
        dto.setUserId(Long.valueOf(match.getUser().getId()));

        // Skip Set mapping for now (set to null), as agreed
        dto.setSets(null);

        return dto;
    }

    // --- Other Controller Methods ---

    // Example placeholder for another method
    @GetMapping("/{matchId}")
    public ResponseEntity<MatchResponseDTO> getMatchById(@PathVariable Long matchId) {
        // Implementation...
        return ResponseEntity.ok(matchService.getMatchById(matchId));
    }

    // Example placeholder for listing all matches
    @GetMapping
    public ResponseEntity<List<MatchResponseDTO>> getAllMatches() {
        // Implementation...
        return ResponseEntity.ok(matchService.getAllMatches());
    }

}