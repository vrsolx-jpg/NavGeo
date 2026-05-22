// ================================================================
// Archivo: src/main/java/com/navgeo/entity/RutaParaderoId.java
// ================================================================
package com.navgeo.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * RutaParaderoId - Clave primaria compuesta para la tabla ruta_paradero.
 *
 * JPA exige que las claves compuestas implementen Serializable y definan
 * equals() y hashCode() basados en TODOS los campos que componen la clave.
 * Lombok genera esos métodos automáticamente con @EqualsAndHashCode.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RutaParaderoId implements Serializable {

    private Long rutaId;
    private Long paraderoId;
}
