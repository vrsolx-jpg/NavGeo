package com.navgeo.repository;

import com.navgeo.entity.EstadisticaParadero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EstadisticaParaderoRepository extends JpaRepository<EstadisticaParadero, Long> {

    Optional<EstadisticaParadero> findByParaderoIdAndFecha(Long paraderoId, LocalDate fecha);
}