package co.com.pragma.autenticacion.model.role.gateways;

import co.com.pragma.autenticacion.model.role.Role;
import reactor.core.publisher.Mono;

/**
 * Puerto (interfaz) que define las operaciones del repositorio de roles.
 *
 * 🔹 Forma parte de la arquitectura hexagonal en la capa de dominio.
 * 🔹 Define qué operaciones deben implementarse, pero no cómo (eso lo hará la capa de infraestructura).
 * 🔹 Usa programación reactiva (Project Reactor), por eso devuelve Mono<Role>.
 */
public interface RoleRepository {

    /**
     * Buscar un rol por su ID único.
     * @param id identificador único del rol.
     * @return Mono<Role> con el rol encontrado o vacío si no existe.
     */
    Mono<Role> findById(Long id);

    /**
     * Guardar un nuevo role en el sistema.
     * @param role objeto de dominio a persistir.
     * @return Mono<Role> con el role guardado.
     */
    Mono<Role> save(Role role);

    /**
     * Actualizar un role existente.
     * @param role objeto con los nuevos valores.
     * @return Mono<Role> con el role actualizado.
     */
    Mono<Role> update(Role role);

    /**
     * Eliminar un rol por su ID.
     * @param id identificador del rol.
     * @return Mono<Void> operación reactiva sin valor de retorno.
     */
    Mono<Void> delete(Long id);
}
