package com.navgeo.controller;

import com.navgeo.dto.CoordenadasRequestDTO;
import com.navgeo.dto.ParaderoRequestDTO;
import com.navgeo.dto.RutaCompletaRequestDTO;
import com.navgeo.entity.Paradero;
import com.navgeo.entity.Ruta;
import com.navgeo.entity.Usuario;
import com.navgeo.repository.ParaderoRepository;
import com.navgeo.repository.RutaRepository;
import com.navgeo.repository.UsuarioRepository;
import com.navgeo.service.CoordenadasService;
import com.navgeo.service.EstadisticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/api")
@RequiredArgsConstructor
public class AdminApiController {

    private final CoordenadasService  coordenadasService;
    private final EstadisticaService  estadisticaService;
    private final RutaRepository      rutaRepository;
    private final ParaderoRepository  paraderoRepository;
    private final UsuarioRepository   usuarioRepository;
    private final PasswordEncoder     passwordEncoder;



    // ── EDITAR RUTA ───────────────────────────────────────────────────

    /**
     * PUT /admin/api/rutas/{id}
     * Edita el nombre, descripción o color de una ruta existente.
     */
    @PutMapping("/rutas/{id}")
    public ResponseEntity<?> editarRuta(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        return rutaRepository.findById(id)
                .map(ruta -> {
                    if (body.containsKey("nombre"))
                        ruta.setNombre(body.get("nombre"));
                    if (body.containsKey("descripcion"))
                        ruta.setDescripcion(body.get("descripcion"));
                    if (body.containsKey("color"))
                        ruta.setColor(body.get("color"));
                    rutaRepository.save(ruta);

                    Map<String, Object> r = new HashMap<>();
                    r.put("mensaje", "Ruta actualizada correctamente.");
                    r.put("id",      ruta.getId());
                    return ResponseEntity.ok(r);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /admin/api/rutas/{id}/coordenadas
     * Reemplaza el trazado completo de una ruta (ida o vuelta).
     */
    @PutMapping("/rutas/{id}/coordenadas")
    public ResponseEntity<String> editarCoordenadas(
            @PathVariable Long id,
            @RequestBody CoordenadasRequestDTO dto) {

        if (!rutaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        dto.setRutaId(id);
        coordenadasService.guardarCoordenadas(dto);
        return ResponseEntity.ok("Trazado actualizado correctamente.");
    }

// ── ELIMINAR RUTA ─────────────────────────────────────────────────

    /**
     * DELETE /admin/api/rutas/{id}
     * Elimina una ruta y todas sus coordenadas (CASCADE en BD).
     */
    @DeleteMapping("/rutas/{id}")
    public ResponseEntity<?> eliminarRuta(@PathVariable Long id) {
        if (!rutaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        rutaRepository.deleteById(id);
        Map<String, Object> r = new HashMap<>();
        r.put("mensaje", "Ruta eliminada correctamente.");
        return ResponseEntity.ok(r);
    }

// ── EDITAR PARADERO ───────────────────────────────────────────────

    /**
     * PUT /admin/api/paraderos/{id}
     * Edita el nombre o ubicación de un paradero.
     */
    @PutMapping("/paraderos/{id}")
    public ResponseEntity<?> editarParadero(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        return paraderoRepository.findById(id)
                .map(paradero -> {
                    if (body.containsKey("nombre"))
                        paradero.setNombre(body.get("nombre"));
                    if (body.containsKey("latitud"))
                        paradero.setLatitud(new BigDecimal(body.get("latitud")));
                    if (body.containsKey("longitud"))
                        paradero.setLongitud(new BigDecimal(body.get("longitud")));
                    if (body.containsKey("color"))
                        paradero.setColor(body.get("color"));
                    paraderoRepository.save(paradero);

                    Map<String, Object> r = new HashMap<>();
                    r.put("mensaje", "Paradero actualizado correctamente.");
                    r.put("id",      paradero.getId());
                    return ResponseEntity.ok(r);
                })
                .orElse(ResponseEntity.notFound().build());
    }

// ── ELIMINAR PARADERO ─────────────────────────────────────────────

    /**
     * DELETE /admin/api/paraderos/{id}
     * Elimina un paradero de la base de datos.
     */
    @DeleteMapping("/paraderos/{id}")
    public ResponseEntity<?> eliminarParadero(@PathVariable Long id) {
        if (!paraderoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        paraderoRepository.deleteById(id);
        Map<String, Object> r = new HashMap<>();
        r.put("mensaje", "Paradero eliminado correctamente.");
        return ResponseEntity.ok(r);
    }

// ── EDITAR EMPLEADO EDITOR ───────────────────────────────────────

    /**
     * PUT /admin/api/editores/{id}
     * Edita datos opcionales de un empleado editor.
     */
    @PutMapping("/editores/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> editarEditor(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        return usuarioRepository.findById(id)
                .filter(usuario -> usuario.getRoles().stream()
                        .anyMatch(r -> r.getNombre().equals("ROLE_EDITOR")))
                .<ResponseEntity<?>>map(editor -> {
                    if (body.containsKey("nombre") && !body.get("nombre").isBlank()) {
                        editor.setNombre(body.get("nombre").trim());
                    }
                    if (body.containsKey("email") && !body.get("email").isBlank()) {
                        String email = body.get("email").trim().toLowerCase();
                        boolean emailUsadoPorOtro = usuarioRepository.findByEmail(email)
                                .map(usuario -> !usuario.getId().equals(editor.getId()))
                                .orElse(false);
                        if (emailUsadoPorOtro) {
                            Map<String, Object> r = new HashMap<>();
                            r.put("mensaje", "Ya existe un usuario registrado con ese correo.");
                            return ResponseEntity.badRequest().body(r);
                        }
                        editor.setEmail(email);
                        editor.setUsername(email);
                    }
                    if (body.containsKey("password") && !body.get("password").isBlank()) {
                        editor.setPassword(passwordEncoder.encode(body.get("password")));
                    }
                    usuarioRepository.save(editor);

                    Map<String, Object> r = new HashMap<>();
                    r.put("mensaje", "Empleado editor actualizado correctamente.");
                    r.put("id", editor.getId());
                    return ResponseEntity.ok(r);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /admin/api/editores/{id}
     * Elimina un empleado editor.
     */
    @DeleteMapping("/editores/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarEditor(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .filter(usuario -> usuario.getRoles().stream()
                        .anyMatch(r -> r.getNombre().equals("ROLE_EDITOR")))
                .<ResponseEntity<?>>map(editor -> {
                    usuarioRepository.delete(editor);
                    Map<String, Object> r = new HashMap<>();
                    r.put("mensaje", "Empleado editor eliminado correctamente.");
                    return ResponseEntity.ok(r);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /admin/api/rutas/completa
     * Crea la ruta y guarda sus coordenadas en una sola operación.
     */
    @PostMapping("/rutas/completa")
    public ResponseEntity<?> crearRutaCompleta(
            @RequestBody RutaCompletaRequestDTO dto) {

        Ruta ruta = new Ruta();
        ruta.setNombre(dto.getNombre());
        ruta.setDescripcion(dto.getDescripcion());
        ruta.setColor(dto.getColor());
        ruta.setActiva(true);
        ruta = rutaRepository.save(ruta);

        CoordenadasRequestDTO coordDTO = new CoordenadasRequestDTO();
        coordDTO.setRutaId(ruta.getId());
        coordDTO.setSentido(dto.getSentido());
        coordDTO.setCoordenadas(dto.getCoordenadas());
        coordenadasService.guardarCoordenadas(coordDTO);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id",      ruta.getId());
        respuesta.put("nombre",  ruta.getNombre());
        respuesta.put("mensaje", "Ruta creada correctamente.");
        return ResponseEntity.ok(respuesta);
    }

    /**
     * POST /admin/api/rutas/{id}/coordenadas
     * Actualiza solo las coordenadas de una ruta existente.
     */
    @PostMapping("/rutas/{id}/coordenadas")
    public ResponseEntity<String> guardarCoordenadas(
            @PathVariable Long id,
            @RequestBody CoordenadasRequestDTO dto) {

        if (!rutaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        dto.setRutaId(id);
        coordenadasService.guardarCoordenadas(dto);
        return ResponseEntity.ok("Coordenadas guardadas correctamente.");
    }

    /**
     * POST /admin/api/paraderos
     * Crea un nuevo paradero con su ubicación geográfica.
     */
    @PostMapping("/paraderos")
    public ResponseEntity<?> crearParadero(
            @RequestBody ParaderoRequestDTO dto) {

        Paradero paradero = new Paradero();
        paradero.setNombre(dto.getNombre());
        paradero.setLatitud(BigDecimal.valueOf(dto.getLatitud()));
        paradero.setLongitud(BigDecimal.valueOf(dto.getLongitud()));
        paradero.setActivo(true);
        paradero.setColor(dto.getColor() != null ? dto.getColor() : "#4F46E5");
        paraderoRepository.save(paradero);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id",      paradero.getId());
        respuesta.put("nombre",  paradero.getNombre());
        respuesta.put("mensaje", "Paradero guardado correctamente.");
        return ResponseEntity.ok(respuesta);
    }

    /**
     * GET /admin/api/dashboard/stats
     * Retorna métricas del sistema para el dashboard.
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> obtenerStats() {
        return ResponseEntity.ok(estadisticaService.obtenerResumenDashboard());


    }
}
