package com.navgeo.controller;

import com.navgeo.entity.CoordTrayecto;
import com.navgeo.entity.Ruta;
import com.navgeo.repository.RutaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RutaController - Controlador REST que expone la API pública de rutas.
 *
 * Este controlador es público (no requiere autenticación) según la política
 * definida en SecurityConfig. Su propósito es proveer al frontend JavaScript
 * los datos en formato JSON que Google Maps necesita para dibujar las
 * polilíneas de las rutas.
 *
 * La anotación @RestController combina @Controller + @ResponseBody, lo que
 * significa que los métodos no retornan nombres de vistas (Thymeleaf), sino
 * que el objeto retornado se serializa automáticamente a JSON por Jackson
 * (incluido por defecto en Spring Boot).
 *
 * La URL base /api/rutas está registrada como pública en SecurityConfig.
 */
@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final RutaRepository rutaRepository;

    /**
     * GET /api/rutas
     * Retorna la lista de todas las rutas activas (sin sus coordenadas).
     * El frontend usa este endpoint para poblar el menú lateral de selección.
     *
     * Ejemplo de respuesta JSON:
     * [
     *   { "id": 1, "nombre": "Ruta 11", "descripcion": "...", "color": "#E53935" },
     *   { "id": 2, "nombre": "Ruta 19", "descripcion": "...", "color": "#1E88E5" }
     * ]
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarRutas() {

        List<Map<String, Object>> rutas = rutaRepository.findByActivaTrue()
                .stream()
                .map(ruta -> {
                    // Al usar un HashMap explícito, Java sabe exactamente
                    // que el tipo es Map<String, Object> y acepta cualquier valor.
                    Map<String, Object> mapa = new HashMap<>();
                    mapa.put("id",          ruta.getId());
                    mapa.put("nombre",      ruta.getNombre());
                    mapa.put("descripcion", ruta.getDescripcion());
                    mapa.put("color",       ruta.getColor());
                    return mapa;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(rutas);
    }

    /**
     * GET /api/rutas/{id}/coordenadas?sentido=ida
     * Retorna el trayecto de una ruta específica, filtrado por sentido.
     *
     * El frontend JavaScript llama a este endpoint cuando el usuario
     * selecciona una ruta en el menú lateral, y dibuja la polilínea con
     * las coordenadas recibidas.
     *
     * @param id      el ID de la ruta en la base de datos.
     * @param sentido "ida" o "vuelta". Por defecto retorna "ida".
     *
     * Ejemplo de respuesta JSON:
     * {
     *   "rutaNombre": "Ruta 11",
     *   "color": "#E53935",
     *   "sentido": "ida",
     *   "coordenadas": [
     *     { "lat": 2.9273, "lng": -75.2819, "orden": 0 },
     *     { "lat": 2.9301, "lng": -75.2785, "orden": 1 }
     *   ]
     * }
     */
    @GetMapping("/{id}/coordenadas")
    public ResponseEntity<?> obtenerCoordenadas(
            @PathVariable Long id,
            @RequestParam(defaultValue = "ida") String sentido) {

        return rutaRepository.findById(id)
                .map(ruta -> {

                    // Filtra las coordenadas por sentido y las convierte a un
                    // formato plano que JavaScript pueda consumir directamente.
                    List<Map<String, Object>> coords = ruta.getCoordenadas()
                            .stream()
                            .filter(c -> c.getSentido().equals(sentido))
                            .sorted((a, b) -> a.getOrden().compareTo(b.getOrden()))
                            .map(c -> Map.<String, Object>of(
                                    "lat",   c.getLatitud(),
                                    "lng",   c.getLongitud(),
                                    "orden", c.getOrden()
                            ))
                            .collect(Collectors.toList());

                    Map<String, Object> respuesta = new HashMap<>();
                    respuesta.put("rutaNombre",  ruta.getNombre());
                    respuesta.put("color",       ruta.getColor());
                    respuesta.put("sentido",     sentido);
                    respuesta.put("coordenadas", coords);
                    return ResponseEntity.ok(respuesta);

                })
                // Si la ruta no existe, retorna 404 con un mensaje claro.
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
