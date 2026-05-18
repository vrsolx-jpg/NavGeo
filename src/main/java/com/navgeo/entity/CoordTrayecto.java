package com.navgeo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * CoordTrayecto - Entidad JPA que representa un punto geográfico del trayecto.
 *
 * Cada instancia de esta clase es un vértice de la polilínea que se dibuja
 * en Google Maps para representar el recorrido de una ruta.
 *
 * El campo 'orden' es fundamental: Google Maps dibuja la Polyline
 * uniendo los puntos EN EL ORDEN EN QUE SE RECIBEN. Si los puntos llegan
 * desordenados, la línea se verá incorrecta (zigzag). Por eso la consulta
 * siempre debe ordenar por 'orden ASC'.
 */
@Entity
@Table(
    name = "coordenadas_trayecto",
    indexes = {
        // Replica el índice compuesto del script SQL para acelerar las consultas frecuentes.
        @Index(name = "idx_coordenadas_ruta_sentido_orden",
               columnList = "ruta_id, sentido, orden")
    }
)
@Getter
@Setter
@NoArgsConstructor
public class CoordTrayecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación ManyToOne: muchos puntos pertenecen a una sola ruta.
     * @JoinColumn especifica el nombre exacto de la columna FK en la BD.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id", nullable = false)
    private Ruta ruta;

    /**
     * Dirección del recorrido. Valores permitidos: "ida" o "vuelta".
     * La restricción CHECK en la BD garantiza que no entre ningún otro valor.
     */
    @Column(nullable = false, length = 10)
    private String sentido;

    /**
     * Latitud en grados decimales. La precisión DECIMAL(10,7) permite
     * una precisión de ~1 cm, más que suficiente para mapas urbanos.
     */
    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitud;


    /** Longitud en grados decimales. Misma precisión que latitud. */
    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitud;
    /**
     * Posición del punto en la secuencia del trayecto (0-indexado).
     * La polilínea en Google Maps conecta los puntos en este orden.
     */
    @Column(nullable = false)
    private Integer orden;
}
