// ================================================================
// Archivo: src/main/java/com/navgeo/repository/RutaRepository.java
// ================================================================
package com.navgeo.repository;

import com.navgeo.entity.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * RutaRepository - Repositorio JPA para la entidad Ruta.
 *
 * Los métodos personalizados siguen la convención de nomenclatura de
 * Spring Data JPA, que traduce automáticamente el nombre del método
 * a la consulta SQL correspondiente.
 */
@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {

    /** Retorna únicamente las rutas activas (para mostrar en el mapa). */
    List<Ruta> findByActivaTrue();

    /** Busca una ruta por su nombre exacto, ej: "Ruta 11". */
    Optional<Ruta> findByNombre(String nombre);
}
