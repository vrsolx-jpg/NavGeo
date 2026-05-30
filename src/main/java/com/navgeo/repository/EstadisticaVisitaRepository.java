package com.navgeo.repository;

import com.navgeo.entity.EstadisticaVisita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EstadisticaVisitaRepository extends JpaRepository<EstadisticaVisita, Long> {

    Optional<EstadisticaVisita> findByFecha(LocalDate fecha);
}