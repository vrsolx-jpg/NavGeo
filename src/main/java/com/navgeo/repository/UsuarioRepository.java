// ================================================================
// Archivo: src/main/java/com/navgeo/repository/UsuarioRepository.java
// ================================================================
package com.navgeo.repository;

import com.navgeo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UsuarioRepository - Repositorio JPA para la entidad Usuario.
 *
 * Al extender JpaRepository<Usuario, Long>, Spring Data JPA genera
 * automáticamente en tiempo de ejecución las implementaciones de los métodos
 * CRUD básicos (save, findById, findAll, delete, etc.). No necesitamos
 * escribir SQL ni código de acceso a datos para operaciones comunes.
 *
 * El método findByUsername es una "Query Method": Spring Data JPA lee
 * el nombre del método y genera la consulta SQL equivalente de forma
 * automática → SELECT * FROM usuarios WHERE username = ?
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     * Retorna Optional para manejar el caso en que el usuario no exista,
     * evitando NullPointerException y siguiendo las buenas prácticas de Java 8+.
     */
    Optional<Usuario> findByUsername(String username);
}
