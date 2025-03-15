package com.manna.fobe.matching.repository;

import com.manna.fobe.matching.entity.Atmosphere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AtmosphereRepository extends JpaRepository<Atmosphere, Long> {
    Optional<Atmosphere> findByAtmosphereId(int atmosphereId);

}
