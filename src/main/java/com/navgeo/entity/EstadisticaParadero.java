package com.navgeo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "estadisticas_paraderos")
@Getter
@Setter
@NoArgsConstructor
public class EstadisticaParadero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paradero_id", nullable = false)
    private Paradero paradero;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Integer consultas = 0;
}