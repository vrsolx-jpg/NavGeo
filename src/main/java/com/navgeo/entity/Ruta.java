package com.navgeo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Ruta - Entidad JPA que representa una ruta de transporte público en Neiva.
 *
 * La relación con CoordTrayecto es @OneToMany: una ruta tiene muchos puntos
 * de coordenadas. Se usa CascadeType.ALL para que al guardar o eliminar una
 * ruta, sus coordenadas se gestionen automáticamente.
 *
 * mappedBy = "ruta" indica que el lado "dueño" de la relación está en la
 * entidad CoordTrayecto (es quien tiene la columna ruta_id en la BD).
 */
@Entity
@Table(name = "rutas")
@Getter
@Setter
@NoArgsConstructor
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 1000)
    private String nombre;

    @Column(nullable = false, length = 300)
    private String descripcion;

    /**
     * Color hexadecimal para renderizar la polilínea en Google Maps.
     * La restricción CHECK en la BD garantiza el formato #RRGGBB.
     */
    @Column(nullable = false, length = 7)
    private String color;

    @Column(nullable = false)
    private boolean activa = true;

    /**
     * Lista de coordenadas que forman el trayecto.
     * orphanRemoval = true: si se elimina un punto de la lista, se borra de la BD.
     */
    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sentido ASC, orden ASC")
    private List<CoordTrayecto> coordenadas = new ArrayList<>();
}
