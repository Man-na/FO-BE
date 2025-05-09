package com.manna.fobe.matching.repository;

import com.manna.fobe.matching.entity.MatchingParticipant;
import com.manna.fobe.matching.entity.MatchingResult;
import com.manna.fobe.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchingParticipantRepository extends JpaRepository<MatchingParticipant, Long> {
    List<MatchingParticipant> findByMatchingResult(MatchingResult matchingResult);

    List<MatchingParticipant> findByUser(User user);

    Optional<MatchingParticipant> findByMatchingResultAndUser(MatchingResult matchingResult, User user);

    List<MatchingParticipant> findByAttendanceStatus(MatchingParticipant.AttendanceStatus status);
}