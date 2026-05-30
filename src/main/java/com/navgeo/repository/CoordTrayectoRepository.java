package com.navgeo.repository;

import com.navgeo.entity.CoordTrayecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CoordTrayectoRepository extends JpaRepository<CoordTrayecto, Long> {

    @Modifying
    @Query("DELETE FROM CoordTrayecto c WHERE c.ruta.id = :rutaId AND c.sentido = :sentido")
    void deleteByRutaIdAndSentido(@Param("rutaId") Long rutaId,
                                  @Param("sentido") String sentido);
}