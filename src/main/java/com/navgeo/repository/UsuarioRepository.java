package com.navgeo.repository;

import com.navgeo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por username cargando sus roles en la misma consulta
     * SQL (JOIN FETCH). Esto evita el problema N+1 que ocurriría si Hibernate
     * cargara los roles con una query separada por cada usuario.
     */
    @Query("SELECT u FROM Usuario u JOIN FETCH u.roles WHERE u.username = :username")
    Optional<Usuario> findByUsernameWithRoles(@Param("username") String username);

    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles WHERE LOWER(TRIM(u.email)) = LOWER(TRIM(:email))")
    Optional<Usuario> findByEmailWithRoles(@Param("email") String email);

    Optional<Usuario> findByEmail(String email);

    // Útil para validar duplicados al registrar un nuevo usuario
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
