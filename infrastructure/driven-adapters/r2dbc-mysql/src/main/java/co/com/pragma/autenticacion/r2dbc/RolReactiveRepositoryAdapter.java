package co.com.pragma.autenticacion.r2dbc;

import co.com.pragma.autenticacion.model.rol.Rol;
import co.com.pragma.autenticacion.model.rol.gateways.RolRepository;
import co.com.pragma.autenticacion.r2dbc.entity.RoleEntity;
import co.com.pragma.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.autenticacion.r2dbc.mapper.RolMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Adaptador que implementa RolRepository del dominio.
 *
 * Explicación:
 * - Este adaptador conecta la capa de dominio (Rol) con la capa de infraestructura (RoleEntity en BD).
 * - Implementa el contrato RolRepository definido en el dominio.
 * - Usa un mapper (RolMapper) para convertir entre entidades de persistencia y modelos de dominio.
 * - Extiende ReactiveAdapterOperations para reutilizar operaciones genéricas de CRUD.
 */
@Repository
public class RolReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Rol, RoleEntity, Integer, RolReactiveRepository>
        implements RolRepository {

    private final RolMapper rolMapper;
    private final RolReactiveRepository rolReactiveRepository;

    /**
     * Constructor:
     * - Se inyectan el repositorio reactivo de roles y el mapper.
     * - Se pasa el mapper::toModel a la superclase para que convierta de Entity -> Domain automáticamente.
     * - El parámetro "null" indica que no usamos ReactiveCommons en este caso.
     */
    protected RolReactiveRepositoryAdapter(RolReactiveRepository rolReactiveRepository, RolMapper rolMapper) {
        super(rolReactiveRepository, null, rolMapper::toModel);
        this.rolReactiveRepository = rolReactiveRepository;
        this.rolMapper = rolMapper;
    }

    /**
     * Busca un rol por ID.
     * - Convierte el ID de Long a Integer porque la entidad usa Integer.
     * - Si lo encuentra, lo convierte de entidad a dominio con el mapper.
     */
    @Override
    public Mono<Rol> findById(Long id) {
        return rolReactiveRepository.findById(id.intValue())
                .map(rolMapper::toModel);
    }

    /**
     * Guarda un nuevo rol en la base de datos.
     * - Convierte de dominio a entidad.
     * - Guarda en la BD.
     * - Convierte la entidad guardada nuevamente a dominio.
     */
    @Override
    public Mono<Rol> save(Rol rol) {
        return rolReactiveRepository.save(rolMapper.toEntity(rol))
                .map(rolMapper::toModel);
    }

    /**
     * Actualiza un rol existente.
     * - Primero busca si el rol existe.
     * - Si existe, convierte el nuevo estado a entidad pero conserva el ID original.
     * - Guarda los cambios y retorna el rol actualizado en modelo de dominio.
     */
    @Override
    public Mono<Rol> update(Rol rol) {
        return rolReactiveRepository.findById(rol.getUniqueId())
                .flatMap(existing -> {
                    RoleEntity entity = rolMapper.toEntity(rol);
                    entity.setIdRol(existing.getIdRol()); // preserva el ID original para no crear uno nuevo
                    return rolReactiveRepository.save(entity);
                })
                .map(rolMapper::toModel);
    }

    /**
     * Elimina un rol por ID.
     * - Convierte el ID de Long a Integer porque la entidad lo maneja así.
     */
    @Override
    public Mono<Void> delete(Long id) {
        return rolReactiveRepository.deleteById(id.intValue());
    }
}
