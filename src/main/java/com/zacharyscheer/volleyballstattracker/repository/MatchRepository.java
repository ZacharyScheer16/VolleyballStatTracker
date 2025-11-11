package com.zacharyscheer.volleyballstattracker.repository;

import com.zacharyscheer.volleyballstattracker.models.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    /**
     * Finds all matches for a user, ordered by the date the match was played,
     * from newest to oldest. This replaces the invalid 'MatchScore' order.
     */
    List<Match> findByUserIdOrderByMatchDateDesc(Integer userId);
    List<Match> findTop10ByUser_IdOrderByMatchDateDesc(Integer userId);
}