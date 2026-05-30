package com.navgeo.security;

import com.navgeo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UsuarioDetailsService - Puente entre Spring Security y la base de datos.
 *
 * Cuando un usuario intenta iniciar sesión, Spring Security llama a
 * loadUserByUsername() para obtener sus datos y comparar el hash
 * de la contraseña ingresada con el hash almacenado en la BD.
 *
 * @Transactional es necesario porque Usuario tiene @ManyToMany con Rol.
 * Sin esta anotación, Hibernate cerraría la sesión antes de cargar
 * los roles y lanzaría LazyInitializationException (aunque se use
 * EAGER, el @Transactional garantiza consistencia).
 */
@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        return usuarioRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + username
                ));
    }
}