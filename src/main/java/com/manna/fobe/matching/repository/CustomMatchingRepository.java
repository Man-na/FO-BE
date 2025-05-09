package com.manna.fobe.matching.repository;

import com.manna.fobe.matching.entity.CustomMatching;
import com.manna.fobe.matching.entity.RapidMatching;
import com.manna.fobe.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CustomMatchingRepository extends JpaRepository<CustomMatching, Long> {
    @Query("SELECT cm FROM CustomMatching cm JOIN cm.atmospheres atm WHERE cm.status = 'PENDING' AND cm.meetingDate = ?1 AND cm.location = ?2 AND atm.atmosphere IN ?3 GROUP BY cm HAVING COUNT(DISTINCT atm.atmosphere) >= 1")
    List<CustomMatching> findCustomMatchingByAtmosphere(String meetingDate, String location, List<String> atmospheres);

}