package co.com.pragma.autenticacion.model.rol.gateways;

import co.com.pragma.autenticacion.model.rol.Rol;
import reactor.core.publisher.Mono;

/**
 * Puerto (interfaz) que define las operaciones del repositorio de roles.
 *
 * 🔹 Forma parte de la arquitectura hexagonal en la capa de dominio.
 * 🔹 Define qué operaciones deben implementarse, pero no cómo (eso lo hará la capa de infraestructura).
 * 🔹 Usa programación reactiva (Project Reactor), por eso devuelve Mono<Rol>.
 */
public interface RolRepository {

    /**
     * Buscar un rol por su ID único.
     * @param id identificador único del rol.
     * @return Mono<Rol> con el rol encontrado o vacío si no existe.
     */
    Mono<Rol> findById(Long id);

    /**
     * Guardar un nuevo rol en el sistema.
     * @param rol objeto de dominio a persistir.
     * @return Mono<Rol> con el rol guardado.
     */
    Mono<Rol> save(Rol rol);

    /**
     * Actualizar un rol existente.
     * @param rol objeto con los nuevos valores.
     * @return Mono<Rol> con el rol actualizado.
     */
    Mono<Rol> update(Rol rol);

    /**
     * Eliminar un rol por su ID.
     * @param id identificador del rol.
     * @return Mono<Void> operación reactiva sin valor de retorno.
     */
    Mono<Void> delete(Long id);
}
