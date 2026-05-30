package com.navgeo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CoordenadasRequestDTO {

    private Long rutaId;
    private String sentido;
    private List<PuntoDTO> coordenadas;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PuntoDTO {
        private BigDecimal lat;
        private BigDecimal lng;
    }
}