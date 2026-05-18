package com.navgeo.security;

import com.navgeo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UsuarioDetailsService - Implementación de la interfaz UserDetailsService.
 *
 * Este servicio actúa como el "puente" entre Spring Security y la base de
 * datos. Cuando un usuario intenta iniciar sesión, Spring Security llama a
 * loadUserByUsername() para obtener el registro del usuario y luego compara
 * el hash de la contraseña ingresada con el hash almacenado en la BD.
 *
 * La anotación @Transactional es importante aquí porque la entidad Usuario
 * tiene relaciones JPA, y esta anotación garantiza que la sesión de Hibernate
 * esté abierta durante toda la ejecución del método, evitando el error
 * LazyInitializationException.
 */
@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Carga un usuario desde la base de datos dado su nombre de usuario.
     *
     * Spring Security invoca este método automáticamente durante el proceso
     * de autenticación. El método retorna un UserDetails (nuestra entidad
     * Usuario ya lo implementa), o lanza UsernameNotFoundException si no
     * se encuentra el usuario, lo que resulta en un error 401.
     *
     * @param username el nombre de usuario ingresado en el formulario de login.
     * @return el objeto Usuario con sus datos y rol cargados desde la BD.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado en la base de datos: " + username
                ));
    }
}
