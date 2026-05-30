package com.navgeo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Paradero - Entidad JPA que representa un paradero de transporte público.
 *
 * Los paraderos son entidades independientes de las rutas. Esta decisión
 * de diseño permite que un paradero sea compartido por múltiples rutas
 * en el futuro, sin duplicar datos geográficos.
 */
@Entity
@Table(name = "paraderos")
@Getter
@Setter
@NoArgsConstructor
public class Paradero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitud;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitud;

    @Column(length = 7)
    private String color = "#4F46E5";  // valor por defecto

    @Column(nullable = false)
    private boolean activo = true;
}
