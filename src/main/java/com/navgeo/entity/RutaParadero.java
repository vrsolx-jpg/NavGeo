// ================================================================
// Archivo: src/main/java/com/navgeo/entity/RutaParadero.java
// ================================================================
package com.navgeo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RutaParadero - Entidad puente entre Ruta y Paradero.
 *
 * Mapea la tabla "ruta_paradero" con clave primaria compuesta
 * (ruta_id, paradero_id) y un campo "orden" que define la
 * secuencia en que los paraderos aparecen dentro de cada ruta.
 *
 * @MapsId garantiza que las FKs (ruta_id, paradero_id) y los campos de la
 * clave embebida apuntan al mismo lugar, evitando columnas duplicadas.
 */
@Entity
@Table(name = "ruta_paradero")
@Getter
@Setter
@NoArgsConstructor
public class RutaParadero {

    @EmbeddedId
    private RutaParaderoId id = new RutaParaderoId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rutaId")
    @JoinColumn(name = "ruta_id")
    private Ruta ruta;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("paraderoId")
    @JoinColumn(name = "paradero_id")
    private Paradero paradero;

    @Column(nullable = false)
    private Integer orden = 0;
}
