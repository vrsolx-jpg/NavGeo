package com.navgeo.service;

import com.navgeo.dto.CoordenadasRequestDTO;
import com.navgeo.entity.CoordTrayecto;
import com.navgeo.entity.Ruta;
import com.navgeo.repository.CoordTrayectoRepository;
import com.navgeo.repository.RutaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoordenadasService {

    private final CoordTrayectoRepository coordRepo;
    private final RutaRepository          rutaRepo;

    /**
     * Reemplaza todas las coordenadas de una ruta+sentido con las nuevas.
     * Estrategia: DELETE de las anteriores + INSERT en lote de las nuevas.
     * El @Transactional garantiza que si algo falla, todo se revierte.
     */
    @Transactional
    public void guardarCoordenadas(CoordenadasRequestDTO dto) {

        Ruta ruta = rutaRepo.findById(dto.getRutaId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ruta no encontrada: " + dto.getRutaId()));

        // 1. Elimina las coordenadas anteriores de esta ruta + sentido
        coordRepo.deleteByRutaIdAndSentido(dto.getRutaId(), dto.getSentido());

        // 2. Construye la lista nueva; el orden = índice en el arreglo
        List<CoordTrayecto> nuevas = new ArrayList<>();
        List<CoordenadasRequestDTO.PuntoDTO> puntos = dto.getCoordenadas();

        for (int i = 0; i < puntos.size(); i++) {
            CoordTrayecto coord = new CoordTrayecto();
            coord.setRuta(ruta);
            coord.setSentido(dto.getSentido());
            coord.setLatitud(puntos.get(i).getLat());
            coord.setLongitud(puntos.get(i).getLng());
            coord.setOrden(i);
            nuevas.add(coord);
        }

        // 3. Insert en lotes de 500 para no saturar memoria
        //    con rutas que tienen miles de puntos
        int batchSize = 500;
        for (int i = 0; i < nuevas.size(); i += batchSize) {
            int fin = Math.min(i + batchSize, nuevas.size());
            coordRepo.saveAll(nuevas.subList(i, fin));
            coordRepo.flush();
        }
    }
}