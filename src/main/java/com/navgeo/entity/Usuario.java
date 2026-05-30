package com.navgeo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Usuario - Entidad JPA que representa a un administrador del sistema NavGeo.
 *
 * Implementa UserDetails de Spring Security para validación directa
 * de credenciales sin conversiones intermedias.
 *
 * CAMBIO RESPECTO A LA VERSIÓN ANTERIOR:
 * El campo String 'rol' fue reemplazado por una relación @ManyToMany
 * con la entidad Rol, a través de la tabla intermedia 'usuarios_roles'.
 * Esto permite asignar múltiples roles a un mismo usuario.
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
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Hash BCrypt de la contraseña. NUNCA se almacena texto plano.
     */
    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    /**
     * Roles asignados al usuario.
     *
     * @ManyToMany: un usuario puede tener varios roles y un rol puede
     *              pertenecer a varios usuarios.
     *
     * fetch = EAGER: los roles se cargan junto con el usuario en la misma
     *                consulta. Necesario para que Spring Security los tenga
     *                disponibles de inmediato al autenticar.
     *
     * @JoinTable: define la tabla intermedia 'usuarios_roles' con sus FKs.
     *
     * cascade = MERGE: si modificas un rol desde el usuario, se propaga.
     *                  NO se usa REMOVE para evitar borrar roles globales.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "usuarios_roles",
            joinColumns        = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

    /**
     * Se ejecuta automáticamente antes del primer INSERT.
     */
    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
    }

    // ---------------------------------------------------------------
    // Métodos helper para manejar roles cómodamente
    // ---------------------------------------------------------------

    /** Agrega un rol al usuario (útil al crear o editar usuarios). */
    public void agregarRol(Rol rol) {
        this.roles.add(rol);
    }

    /** Quita un rol específico del usuario. */
    public void quitarRol(Rol rol) {
        this.roles.remove(rol);
    }

    // ---------------------------------------------------------------
    // Métodos de UserDetails (requeridos por Spring Security)
    // ---------------------------------------------------------------

    /**
     * Retorna los roles del usuario. Como Rol implementa GrantedAuthority,
     * se retorna el Set directamente sin conversiones.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convierte cada Rol a SimpleGrantedAuthority aquí,
        // en lugar de que Rol implemente GrantedAuthority directamente.
        // Esto evita el conflicto entre los proxies de Hibernate y Spring Security.
        return this.roles.stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                .collect(Collectors.toList());
    }
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return activo; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return activo; }
}