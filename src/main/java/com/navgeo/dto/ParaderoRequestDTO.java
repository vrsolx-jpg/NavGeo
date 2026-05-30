package com.navgeo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParaderoRequestDTO {
    private String nombre;
    private Double latitud;
    private Double longitud;
    private String color;
}