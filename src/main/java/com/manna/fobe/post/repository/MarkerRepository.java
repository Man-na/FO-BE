package com.manna.fobe.post.repository;

import com.manna.fobe.post.entity.Marker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarkerRepository extends JpaRepository<Marker, Long> {
    List<Marker> findByUserId(int userId);
}