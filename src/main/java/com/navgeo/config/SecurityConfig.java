package com.navgeo.config;

import com.navgeo.security.UsuarioDetailsService;
import com.navgeo.security.GoogleOAuth2UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * SecurityConfig - Configuración central de seguridad de NavGeo.
 *
 * Esta clase define las reglas de acceso que rigen toda la plataforma:
 *
 *   PÚBLICA (sin login):
 *     - Página principal del mapa (/)
 *     - Endpoints de la API REST de consulta (/api/rutas/**, /api/paraderos/**)
 *     - Recursos estáticos (CSS, JS, imágenes)
 *     - Página de login (/login)
 *
 *   PROTEGIDA (requiere ROLE_ADMIN):
 *     - Panel de administración (/admin/**)
 *
 * Este diseño cumple con el principio "seguridad por defecto":
 * cualquier ruta no listada explícitamente requiere autenticación.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;
    private final GoogleOAuth2UsuarioService googleOAuth2UsuarioService;

    /**
     * PasswordEncoder - Bean que define el algoritmo de cifrado de contraseñas.
     *
     * BCrypt es el estándar de la industria para contraseñas. A diferencia de
     * MD5 o SHA-1, BCrypt incluye un "salt" aleatorio y permite ajustar el
     * "costo" computacional (parámetro strength). Cuanto mayor es el costo,
     * más difícil es realizar ataques de fuerza bruta o de diccionario.
     *
     * La fortaleza 12 es un buen balance entre seguridad y rendimiento para
     * una aplicación académica/institucional.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * DaoAuthenticationProvider - Conecta Spring Security con nuestra BD.
     *
     * Este proveedor le indica a Spring Security:
     *  1. Cómo cargar un usuario (usando UsuarioDetailsService).
     *  2. Cómo verificar la contraseña (usando BCryptPasswordEncoder).
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager - Expone el gestor de autenticación como Bean.
     * Necesario si en el futuro se desea invocar la autenticación
     * programáticamente desde un controlador.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * SecurityFilterChain - Define las reglas de acceso HTTP.
     *
     * Este es el corazón de la configuración de seguridad. Actúa como un
     * filtro que evalúa cada petición HTTP ANTES de que llegue al controlador.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // -------------------------------------------------------
            // REGLAS DE AUTORIZACIÓN (quién puede acceder a qué)
            // -------------------------------------------------------
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/admin/api/**")
                )
            .authorizeHttpRequests(auth -> auth

                // RECURSOS ESTÁTICOS: siempre públicos.
                .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll()

                // API PÚBLICA: consulta de rutas y paraderos para el mapa.
                // Cualquier visitante (sin login) puede obtener este JSON.
                .requestMatchers("/api/rutas/**", "/api/paraderos/**").permitAll()

                // PÁGINA PRINCIPAL: el mapa es de acceso público.
                .requestMatchers("/", "/index", "/login", "/oauth2/**", "/login/oauth2/**").permitAll()

                    .requestMatchers("/api/rutas/**", "/api/paraderos/**").permitAll()

                // Acciones destructivas y gestión de usuarios: solo ADMIN.
                .requestMatchers(HttpMethod.DELETE, "/admin/api/**").hasRole("ADMIN")
                .requestMatchers("/admin/eliminar", "/admin/estadisticas", "/admin/editores").hasRole("ADMIN")
                .requestMatchers("/admin/api/dashboard/**").hasRole("ADMIN")

                    // Swagger UI: acceso público para documentación
                    .requestMatchers(
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**"
                    ).permitAll()

                // Panel operativo: ADMIN y EDITOR.
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "EDITOR")

                // POLÍTICA POR DEFECTO: cualquier otra ruta no listada
                // requiere que el usuario esté autenticado.
                .anyRequest().authenticated()


            )

            // -------------------------------------------------------
            // CONFIGURACIÓN DEL FORMULARIO DE LOGIN
            // -------------------------------------------------------
            .formLogin(login -> login
                // URL donde está el formulario de login (nuestra login.html).
                .loginPage("/login")

                // URL a la que Spring Security redirige tras un login exitoso.
                .defaultSuccessUrl("/admin/dashboard", true)

                // Parámetro en login.html para mostrar el mensaje de error.
                .failureUrl("/login?error=true")

                // Permite el acceso a /login sin autenticación.
                .permitAll()
            )

            // -------------------------------------------------------
            // LOGIN CON GOOGLE
            // -------------------------------------------------------
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureHandler((request, response, exception) ->
                    response.sendRedirect("/login?error=google"))
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(googleOAuth2UsuarioService)
                )
            )

            // -------------------------------------------------------
            // CONFIGURACIÓN DEL LOGOUT
            // -------------------------------------------------------
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)    // Destruye la sesión del servidor.
                .deleteCookies("JSESSIONID")    // Elimina la cookie de sesión.
                .permitAll()
            )

            // -------------------------------------------------------
            // PROVEEDOR DE AUTENTICACIÓN
            // -------------------------------------------------------
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
