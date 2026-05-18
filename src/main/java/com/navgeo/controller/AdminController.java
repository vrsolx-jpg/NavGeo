// ================================================================
// Archivo: src/main/java/com/navgeo/controller/AdminController.java
// ================================================================
package com.navgeo.controller;

import com.navgeo.repository.ParaderoRepository;
import com.navgeo.repository.RutaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * AdminController - Controlador MVC para el panel de administración.
 *
 * TODAS las rutas bajo /admin/** están protegidas por Spring Security
 * (definido en SecurityConfig). Si un usuario sin autenticación intenta
 * acceder a cualquier URL bajo /admin/, Spring Security lo redirige
 * automáticamente a /login, sin que este controlador deba hacer nada extra.
 *
 * La anotación @AuthenticationPrincipal inyecta el objeto UserDetails
 * del usuario actualmente autenticado, lo que nos permite mostrar su
 * nombre en la interfaz del panel.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RutaRepository rutaRepository;
    private final ParaderoRepository paraderoRepository;

    /**
     * GET /admin/dashboard
     * Muestra el panel principal del administrador con estadísticas básicas.
     * Aquí se expanden las funcionalidades de gestión de rutas y paraderos.
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        // Estadísticas básicas para el panel
        model.addAttribute("totalRutas",    rutaRepository.count());
        model.addAttribute("totalParaderos", paraderoRepository.count());
        model.addAttribute("adminNombre",   userDetails.getUsername());

        return "admin/dashboard"; // → templates/admin/dashboard.html
    }
}
