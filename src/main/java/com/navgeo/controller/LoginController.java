// ================================================================
// Archivo: src/main/java/com/navgeo/controller/LoginController.java
// ================================================================
package com.navgeo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * LoginController - Controlador MVC para las páginas de autenticación.
 *
 * A diferencia de RutaController (que usa @RestController y retorna JSON),
 * este controlador usa @Controller y retorna el NOMBRE de una plantilla
 * Thymeleaf. Spring Boot buscará ese archivo en src/main/resources/templates/.
 *
 * Spring Security maneja automáticamente el POST de login, por lo que en
 * este controlador solo se necesita manejar el GET (mostrar el formulario).
 */
@Controller
public class LoginController {

    @Value("${google.maps.api-key:}")
    private String googleMapsApiKey;

    /**
     * GET /
     * Muestra la página principal del mapa (index.html).
     * Es pública, según la configuración de SecurityConfig.
     */
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        return "index";
    }

    /**
     * GET /login
     * Muestra el formulario de login del panel administrativo.
     *
     * Los parámetros error y logout son opcionales. Spring Security los agrega
     * automáticamente a la URL cuando falla el login (/login?error=true) o
     * cuando el usuario cierra sesión (/login?logout=true).
     *
     * Se pasan como atributos del Model para que Thymeleaf pueda mostrar
     * mensajes de retroalimentación en la vista.
     */
    @GetMapping("/login")
    public String loginForm(
            @RequestParam(value = "error",  required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMsg", "Usuario o contraseña incorrectos.");
        }
        if (logout != null) {
            model.addAttribute("logoutMsg", "Sesión cerrada correctamente.");
        }

        return "login"; // → Busca templates/login.html
    }
}
