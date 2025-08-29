package co.com.pragma.autenticacion.model.role;

import lombok.*;

/**
 * Entidad de dominio que representa un Role dentro del sistema.
 *
 * 🔹 Pertenece al modelo del dominio (no depende de infraestructura).
 * 🔹 Representa perfiles como ADMIN, USER, GUEST, etc.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true) // Permite crear y clonar objetos de manera fluida
public class Role {

    /**
     * Identificador único del rol.
     * Ejemplo: 1 -> ADMIN
     */
    private Integer uniqueId;

    /**
     * Nombre del rol.
     * Ejemplo: "ADMIN", "USER".
     *
     */
    private String name;

    /**
     * Descripción detallada del rol.
     * Ejemplo: "Administrador del sistema".
     */
    private String description;
}
