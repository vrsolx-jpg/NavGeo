// ================================================================
// Archivo: src/main/java/com/navgeo/entity/EstadisticaVisita.java
// ================================================================
package com.navgeo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * EstadisticaVisita - Contador diario de visitas a la plataforma.
 *
 * Hay UN registro por día (la columna "fecha" es UNIQUE), y el campo
 * total_visitas se incrementa cada vez que un usuario abre la página
 * principal del mapa.
 */
@Entity
@Table(name = "estadisticas_visitas")
@Getter
@Setter
@NoArgsConstructor
public class EstadisticaVisita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate fecha;

    @Column(name = "total_visitas", nullable = false)
    private Integer totalVisitas = 0;
}
