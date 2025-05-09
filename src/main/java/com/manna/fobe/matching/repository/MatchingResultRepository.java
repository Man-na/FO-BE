package com.manna.fobe.matching.repository;

import com.manna.fobe.matching.entity.MatchingResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MatchingResultRepository extends JpaRepository<MatchingResult, Long> {
    List<MatchingResult> findByMatchingTypeAndMatchingId(MatchingResult.MatchingType matchingType, Long matchingId);

    @Query("SELECT mr FROM MatchingResult mr JOIN mr.participants p WHERE p.user.id = ?1")
    List<MatchingResult> findByUserId(Long userId);

    List<MatchingResult> findByStatus(MatchingResult.ResultStatus status);
}