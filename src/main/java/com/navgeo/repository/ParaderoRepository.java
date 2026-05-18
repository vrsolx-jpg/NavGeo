// ================================================================
// Archivo: src/main/java/com/navgeo/repository/ParaderoRepository.java
// ================================================================
package com.navgeo.repository;

import com.navgeo.entity.Paradero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ParaderoRepository - Repositorio JPA para la entidad Paradero.
 */
@Repository
public interface ParaderoRepository extends JpaRepository<Paradero, Long> {

    /** Retorna únicamente los paraderos activos (para mostrar en el mapa). */
    List<Paradero> findByActivoTrue();
}
