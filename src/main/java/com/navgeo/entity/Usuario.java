package com.navgeo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Usuario - Entidad JPA que representa a un administrador del sistema NavGeo.
 *
 * Esta clase implementa la interfaz UserDetails de Spring Security. Esto permite
 * que Spring Security utilice directamente los objetos Usuario para validar
 * credenciales, sin necesidad de conversiones intermedias.
 *
 * La anotación @Entity indica a JPA que esta clase debe mapearse a una tabla
 * en la base de datos. La anotación @Table especifica el nombre exacto.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de usuario único para el login.
     * La restricción @Column(unique = true) crea una restricción UNIQUE en la BD.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Hash BCrypt de la contraseña. NUNCA se almacena el texto plano.
     * La longitud 255 es suficiente para cualquier hash BCrypt (60-72 chars).
     */
    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Rol del usuario. Spring Security requiere el prefijo "ROLE_".
     * Valores válidos: ROLE_ADMIN, ROLE_VIEWER (validado en la BD con CHECK).
     */
    @Column(nullable = false, length = 20)
    private String rol;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    /**
     * Se ejecuta automáticamente antes del primer INSERT.
     * Garantiza que creado_en siempre tenga valor sin depender de la BD.
     */
    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
    }

    // ---------------------------------------------------------------
    // Métodos de UserDetails (requeridos por Spring Security)
    // ---------------------------------------------------------------

    /**
     * Retorna los roles/permisos del usuario como objetos GrantedAuthority.
     * Spring Security los usa para evaluar reglas como hasRole("ADMIN").
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.rol));
    }

    /** La cuenta está activa si el campo activo = true. */
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return activo; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return activo; }
}
