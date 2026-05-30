// ================================================================
// Archivo: src/main/java/com/navgeo/controller/AdminController.java
// ================================================================
package com.navgeo.controller;

import com.navgeo.entity.Usuario;
import com.navgeo.repository.ParaderoRepository;
import com.navgeo.repository.RutaRepository;
import com.navgeo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.navgeo.entity.Rol;
import com.navgeo.repository.RolRepository;

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
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    // Agrégalo junto a los otros repositorios que ya tienes inyectados
    private final RolRepository rolRepository;

    /**
     * GET /admin/dashboard
     * Muestra el panel principal del administrador con estadísticas básicas.
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(
            Authentication authentication,
            Model model) {

        model.addAttribute("totalRutas",     rutaRepository.count());
        model.addAttribute("totalParaderos", paraderoRepository.count());
        model.addAttribute("adminNombre",    nombreAutenticado(authentication));
        model.addAttribute("esAdmin",        esAdmin(authentication));

        return "admin/dashboard";
    }

    /**
     * GET /admin/rutas
     * Formulario para registrar nuevas rutas (latitud, longitud, sentido).
     * El POST de persistencia aún no está implementado.
     */
    @GetMapping("/rutas")
    public String registrarRutas(
            Authentication authentication,
            Model model) {

        model.addAttribute("adminNombre", nombreAutenticado(authentication));
        return "admin/rutas";
    }

    /**
     * GET /admin/paraderos
     * Formulario para registrar nuevos paraderos.
     */
    @GetMapping("/paraderos")
    public String registrarParaderos(
            Authentication authentication,
            Model model) {

        model.addAttribute("adminNombre", nombreAutenticado(authentication));
        return "admin/paraderos";
    }

    /**
     * GET /admin/eliminar
     * Vista para eliminar rutas o paraderos.
     */
    @GetMapping("/eliminar")
    public String eliminar(
            Authentication authentication,
            Model model) {

        model.addAttribute("adminNombre", nombreAutenticado(authentication));
        return "admin/eliminar";
    }

    /**
     * GET /admin/estadisticas
     * Consulta de estadísticas del sistema.
     */
    @GetMapping("/estadisticas")
    public String estadisticas(
            Authentication authentication,
            Model model) {

        model.addAttribute("adminNombre",    nombreAutenticado(authentication));
        model.addAttribute("totalRutas",     rutaRepository.count());
        model.addAttribute("totalParaderos", paraderoRepository.count());
        return "admin/estadisticas";
    }

    /**
     * GET /admin/editar
     * Vista para editar rutas o paraderos existentes.
     */
    @GetMapping("/editar")
    public String editar(
            Authentication authentication,
            Model model) {

        model.addAttribute("adminNombre", nombreAutenticado(authentication));
        model.addAttribute("esAdmin", esAdmin(authentication));
        return "admin/editar";
    }

    /**
     * GET /admin/editores
     * Formulario para que un administrador registre usuarios editores.
     */
    @GetMapping("/editores")
    @PreAuthorize("hasRole('ADMIN')")
    public String editores(
            Authentication authentication,
            Model model) {

        model.addAttribute("adminNombre", nombreAutenticado(authentication));
        return "admin/editores";
    }

    /**
     * POST /admin/editores
     * Crea un usuario con rol ROLE_EDITOR. El correo se usa también como username.
     */
    @PostMapping("/editores")
    @PreAuthorize("hasRole('ADMIN')")
    public String crearEditor(
            Authentication authentication,
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        String emailNormalizado = email == null ? "" : email.trim().toLowerCase();
        String nombreNormalizado = nombre == null ? "" : nombre.trim();

        model.addAttribute("adminNombre", nombreAutenticado(authentication));
        model.addAttribute("nombreIngresado", nombreNormalizado);
        model.addAttribute("emailIngresado", emailNormalizado);

        if (nombreNormalizado.isBlank() || emailNormalizado.isBlank() || password == null || password.isBlank()) {
            model.addAttribute("errorMsg", "Completa nombre, correo y contraseña.");
            return "admin/editores";
        }

        if (usuarioRepository.existsByUsername(emailNormalizado) || usuarioRepository.existsByEmail(emailNormalizado)) {
            model.addAttribute("errorMsg", "Ya existe un usuario registrado con ese correo.");
            return "admin/editores";
        }


        Rol rolEditor = rolRepository.findByNombre("ROLE_EDITOR")
                .orElseThrow(() -> new RuntimeException(
                        "Rol ROLE_EDITOR no encontrado en la base de datos. " +
                                "Verifique que el script de inserción se ejecutó correctamente."));

        Usuario editor = new Usuario();
        editor.setNombre(nombreNormalizado);
        editor.setEmail(emailNormalizado);
        editor.setUsername(emailNormalizado);
        editor.setPassword(passwordEncoder.encode(password));
        editor.agregarRol(rolEditor);      // ← nuevo método
        editor.setActivo(true);
        usuarioRepository.save(editor);

        model.addAttribute("successMsg", "Editor registrado correctamente.");
        model.addAttribute("nombreIngresado", "");
        model.addAttribute("emailIngresado", "");
        return "admin/editores";
    }

    private String nombreAutenticado(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return "ADMIN";
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        if (principal instanceof OAuth2User oauth2User) {
            String nombre = oauth2User.getAttribute("name");
            if (nombre != null && !nombre.isBlank()) {
                return nombre;
            }
            String email = oauth2User.getAttribute("email");
            if (email != null && !email.isBlank()) {
                return email;
            }
        }
        return authentication.getName();
    }

    private boolean esAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}
