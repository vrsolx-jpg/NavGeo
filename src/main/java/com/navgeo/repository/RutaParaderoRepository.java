package com.navgeo.repository;

import com.navgeo.entity.RutaParadero;
import com.navgeo.entity.RutaParaderoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RutaParaderoRepository extends JpaRepository<RutaParadero, RutaParaderoId> {

    List<RutaParadero> findByRutaId(Long rutaId);

    List<RutaParadero> findByParaderoId(Long paraderoId);

    @Modifying
    @Transactional
    void deleteByRutaId(Long rutaId);
}