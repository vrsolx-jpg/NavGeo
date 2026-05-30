// En un nuevo archivo ParaderoController.java
package com.navgeo.controller;

import com.navgeo.repository.ParaderoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/paraderos")
@RequiredArgsConstructor
public class ParaderoController {

    private final ParaderoRepository paraderoRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarParaderos() {

        List<Map<String, Object>> paraderos = paraderoRepository.findByActivoTrue()
                .stream()
                .map(p -> {
                    Map<String, Object> mapa = new HashMap<>();
                    mapa.put("id",          p.getId());
                    mapa.put("nombre",      p.getNombre());
                    mapa.put("latitud",     p.getLatitud());
                    mapa.put("longitud",    p.getLongitud());
                    mapa.put("color", p.getColor());
                    return mapa;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(paraderos);
    }
}