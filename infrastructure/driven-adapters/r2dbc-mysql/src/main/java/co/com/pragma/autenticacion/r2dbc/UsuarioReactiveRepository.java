package co.com.pragma.autenticacion.r2dbc;

import co.com.pragma.autenticacion.r2dbc.entity.UserEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo para la entidad UserEntity.
 *
 * Explicación:
 * - Extiende ReactiveCrudRepository para operaciones CRUD básicas (findAll, findById, save, delete).
 * - Extiende ReactiveQueryByExampleExecutor para búsquedas dinámicas usando Query by Example.
 * - Define métodos específicos para verificar existencia de usuarios por correo, documento o rol.
 */
public interface UsuarioReactiveRepository extends ReactiveCrudRepository<UserEntity, Long>, ReactiveQueryByExampleExecutor<UserEntity> {

    /**
     * Verifica si existe un usuario con un correo electrónico dado.
     */
    Mono<Boolean> existsByCorreoElectronico(String correoElectronico);

    /**
     * Verifica si existe un usuario con un documento de identidad dado.
     */
    Mono<Boolean> existsByDocumentoIdentidad(String documentoIdentidad);

    /**
     * Verifica si existe un usuario asociado a un rol específico.
     */
    Mono<Boolean> existsByRolId(Long rolId);

}
