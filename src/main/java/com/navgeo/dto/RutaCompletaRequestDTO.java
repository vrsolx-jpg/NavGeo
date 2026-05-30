package com.navgeo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RutaCompletaRequestDTO {

    private String nombre;
    private String descripcion;
    private String color;
    private String sentido;
    private List<CoordenadasRequestDTO.PuntoDTO> coordenadas;
}