package com.manna.fobe.marker.repository;

import com.manna.fobe.marker.entity.Marker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MarkerRepository extends JpaRepository<Marker, Integer> {
    List<Marker> findByUserId(int userId);
    Page<Marker> findByUserId(int userId, Pageable pageable);
    Marker findById(int markerId);
    List<Marker> findByUserIdAndDateBetween(int userId, LocalDate startDate, LocalDate endDate);
}