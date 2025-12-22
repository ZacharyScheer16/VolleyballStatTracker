package com.zacharyscheer.volleyballstattracker.controller;

import com.zacharyscheer.volleyballstattracker.dto.PlayerMatchAggregateStatsDTO;
import com.zacharyscheer.volleyballstattracker.dto.StatLineResponseDTO;
import com.zacharyscheer.volleyballstattracker.dto.TeamMatchAggregateStatsDTO;
import com.zacharyscheer.volleyballstattracker.mapper.StatLineMapper;
import com.zacharyscheer.volleyballstattracker.models.StatLine;
import com.zacharyscheer.volleyballstattracker.service.statLineServices.StatLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatController {

    private final StatLineService statLineService;
    private final StatLineMapper statLineMapper;


    /**
     * Retrieves the current StatLine for a specific player in a set.
     */
    @GetMapping("/set/{setId}/player/{playerId}")
    public ResponseEntity<StatLineResponseDTO> getStats(
            @PathVariable Long setId,
            @PathVariable Integer playerId) {

        StatLine stats = statLineService.getStatLineBySetAndPlayer(setId, playerId);
        // Use the mapper to convert the entity to the DTO
        return ResponseEntity.ok(statLineMapper.toDto(stats));
    }

    // -------------------------------------------------------------------------
    // NEW: MATCH AGGREGATION ENDPOINTS
    // -------------------------------------------------------------------------

    /**
     * Retrieves the aggregated match statistics for a specific player across all sets.
     */
    @GetMapping("/match/{matchId}/player/{playerId}/totals")
    public ResponseEntity<PlayerMatchAggregateStatsDTO> getPlayerMatchTotals(
            @PathVariable Long matchId,
            @PathVariable Integer playerId) {

        PlayerMatchAggregateStatsDTO totals = statLineService.getIndividualMatchTotals(matchId, playerId);
        return ResponseEntity.ok(totals);
    }

    /**
     * Retrieves the aggregated match statistics for the entire team across all sets.
     */
    @GetMapping("/match/{matchId}/team/totals")
    public ResponseEntity<TeamMatchAggregateStatsDTO> getTeamMatchTotals(
            @PathVariable Long matchId) {

        TeamMatchAggregateStatsDTO totals = statLineService.getTeamMatchTotals(matchId);
        return ResponseEntity.ok(totals);
    }


    // -------------------------------------------------------------------------
    // HITTING ENDPOINTS
    // -------------------------------------------------------------------------

    /**
     * Records a Kill and increments AttackAttempts by 1.
     */
    @PostMapping("/record/kill")
    public ResponseEntity<StatLineResponseDTO> recordKill(
            @RequestParam Long setId,
            @RequestParam Integer playerId) {

        StatLine updatedStats = statLineService.recordKill(setId, playerId);
        return ResponseEntity.ok(statLineMapper.toDto(updatedStats));
    }

    /**
     * Records an AttackAttempt (e.g., covered attack or free ball).
     */
    @PostMapping("/record/attack-attempt")
    public ResponseEntity<StatLineResponseDTO> recordAttackAttempt(
            @RequestParam Long setId,
            @RequestParam Integer playerId) {

        StatLine updatedStats = statLineService.recordAttackAttempt(setId, playerId);
        return ResponseEntity.ok(statLineMapper.toDto(updatedStats));
    }

    /**
     * Records a Kill Error (hitting out, blocked, etc.) and increments AttackAttempts by 1.
     */
    @PostMapping("/record/kill-error")
    public ResponseEntity<StatLineResponseDTO> recordKillError(
            @RequestParam Long setId,
            @RequestParam Integer playerId) {

        StatLine updatedStats = statLineService.recordKillError(setId, playerId);
        return ResponseEntity.ok(statLineMapper.toDto(updatedStats));
    }

    // -------------------------------------------------------------------------
    // SERVING ENDPOINTS
    // -------------------------------------------------------------------------

    /**
     * Records a Service Ace and increments ServiceAttempt by 1.
     */
    @PostMapping("/record/service-ace")
    public ResponseEntity<StatLineResponseDTO> recordServiceAce(
            @RequestParam Long setId,
            @RequestParam Integer playerId) {

        StatLine updatedStats = statLineService.recordServiceAce(setId, playerId);
        return ResponseEntity.ok(statLineMapper.toDto(updatedStats));
    }

    /**
     * Records a Service Attempt (in-play serve).
     */
    @PostMapping("/record/service-attempt")
    public ResponseEntity<StatLineResponseDTO> recordServiceAttempt(
            @RequestParam Long setId,
            @RequestParam Integer playerId) {

        StatLine updatedStats = statLineService.recordServiceAttempt(setId, playerId);
        return ResponseEntity.ok(statLineMapper.toDto(updatedStats));
    }

    /**
     * Records an Service error (e.g. in net, out of bounds) and increments ServiceAttempt by 1.
     */
    @PostMapping("/record/service-error")
    public ResponseEntity<StatLineResponseDTO> recordServiceError(
            @RequestParam Long setId,
            @RequestParam Integer playerId) {

        StatLine updatedStats = statLineService.recordServiceError(setId, playerId);
        return ResponseEntity.ok(statLineMapper.toDto(updatedStats));
    }

    // -------------------------------------------------------------------------
    // PASSING ENDPOINTS
    // -------------------------------------------------------------------------

    /**
     * Records a Pass Rating (0, 1, 2, or 3). (Restored)
     */
    @PostMapping("/record/pass-rating")
    public ResponseEntity<StatLineResponseDTO> recordPassRating(
            @RequestParam Long setId,
            @RequestParam Integer playerId,
            @RequestParam int rating) {

        StatLine updatedStats = statLineService.recordPassRating(setId, playerId, rating);
        return ResponseEntity.ok(statLineMapper.toDto(updatedStats));
    }


    // -------------------------------------------------------------------------
    // DEFENSE & BLOCKING ENDPOINTS
    // -------------------------------------------------------------------------

    /**
     * Records a Dig.
     */
    @PostMapping("/record/dig")
    public ResponseEntity<StatLineResponseDTO> recordDig(@RequestParam Long setId, @RequestParam Integer playerId) {
        StatLine updatedStats = statLineService.recordDig(setId, playerId);
        return ResponseEntity.ok(statLineMapper.toDto(updatedStats));
    }

    /**
     * Records a Dig Error. (Fixed mapping to prevent collision with recordDig)
     */
    @PostMapping("/record/dig-error")
    public ResponseEntity<StatLineResponseDTO> recordDigError(@RequestParam Long setId, @RequestParam Integer playerId) {
        StatLine updatedStats = statLineService.recordDigError(setId, playerId);
        return ResponseEntity.ok(statLineMapper.toDto(updatedStats));
    }

    /**
     * Records a Block. (Renamed parameter method to match service)
     */
    @PostMapping("/record/block")
    public ResponseEntity<StatLineResponseDTO> recordBlock(@RequestParam Long setId, @RequestParam Integer playerId) {
        StatLine updatedStats = statLineService.recordBlock(setId, playerId);
        return ResponseEntity.ok(statLineMapper.toDto(updatedStats));
    }

    /**
     * Records a Block Error.
     */
    @PostMapping("/record/block-error")
    public ResponseEntity<StatLineResponseDTO> recordBlockError(@RequestParam Long setId, @RequestParam Integer playerId) {
        StatLine updatedStats = statLineService.recordBlockError(setId, playerId);
        return ResponseEntity.ok(statLineMapper.toDto(updatedStats));
    }


    // -------------------------------------------------------------------------
    // SETTING ENDPOINTS
    // -------------------------------------------------------------------------

    /**
     * Records a Set Attempt (no assist or error). (Fixed mapping name)
     */
    @PostMapping("/record/set-attempt")
    public ResponseEntity<StatLineResponseDTO> recordSetAttempt(@RequestParam Long setId, @RequestParam Integer playerId) {
        StatLine updatedStats = statLineService.recordSetAttempt(setId, playerId);
        return ResponseEntity.ok(statLineMapper.toDto(updatedStats));
    }

    /**
     * Records a Set Error and increments SetAttempt by 1.
     */
    @PostMapping("/record/set-error")
    public ResponseEntity<StatLineResponseDTO> recordSetError(@RequestParam Long setId, @RequestParam Integer playerId) {
        StatLine updatedStats = statLineService.recordSetError(setId, playerId);
        return ResponseEntity.ok(statLineMapper.toDto(updatedStats));
    }

    /**
     * Records an Assist and increments SetAttempt by 1.
     */
    @PostMapping("/record/assist")
    public ResponseEntity<StatLineResponseDTO> recordAssist(@RequestParam Long setId, @RequestParam Integer playerId) {
        StatLine updatedStats = statLineService.recordAssist(setId, playerId);
        return ResponseEntity.ok(statLineMapper.toDto(updatedStats));
    }
}