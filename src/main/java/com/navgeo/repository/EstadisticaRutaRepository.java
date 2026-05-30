package com.navgeo.repository;

import com.navgeo.entity.EstadisticaRuta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EstadisticaRutaRepository extends JpaRepository<EstadisticaRuta, Long> {

    Optional<EstadisticaRuta> findByRutaIdAndFecha(Long rutaId, LocalDate fecha);

    List<EstadisticaRuta> findTop4ByOrderByConsultasDesc();
}
