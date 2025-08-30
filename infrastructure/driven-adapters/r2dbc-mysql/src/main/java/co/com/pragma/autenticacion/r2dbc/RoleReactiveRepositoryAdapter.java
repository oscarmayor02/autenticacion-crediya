package co.com.pragma.autenticacion.r2dbc;

import co.com.pragma.autenticacion.model.role.Role;
import co.com.pragma.autenticacion.model.role.gateways.RoleRepository;
import co.com.pragma.autenticacion.r2dbc.entity.RoleEntity;
import co.com.pragma.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.autenticacion.r2dbc.mapper.RoleMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Adaptador que implementa RoleRepository del dominio.
 *
 * Explicación:
 * - Este adaptador conecta la capa de dominio (Role) con la capa de infraestructura (RoleEntity en BD).
 * - Implementa el contrato RoleRepository definido en el dominio.
 * - Usa un mapper (RoleMapper) para convertir entre entidades de persistencia y modelos de dominio.
 * - Extiende ReactiveAdapterOperations para reutilizar operaciones genéricas de CRUD.
 */
@Repository
public class RoleReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Role, RoleEntity, Integer, RoleReactiveRepository>
        implements RoleRepository {

    private final RoleMapper roleMapper;
    private final RoleReactiveRepository roleReactiveRepository;

    /**
     * Constructor:
     * - Se inyectan el repositorio reactivo de roles y el mapper.
     * - Se pasa el mapper::toModel a la superclase para que convierta de Entity -> Domain automáticamente.
     * - El parámetro "null" indica que no usamos ReactiveCommons en este caso.
     */
    protected RoleReactiveRepositoryAdapter(RoleReactiveRepository roleReactiveRepository,
                                            RoleMapper roleMapper) {
        super(roleReactiveRepository, null, roleMapper::toModel);
        this.roleReactiveRepository = roleReactiveRepository;
        this.roleMapper = roleMapper;
    }

    /**
     * Busca un rol por ID.
     * - Convierte el ID de Long a Integer porque la entidad usa Integer.
     * - Si lo encuentra, lo convierte de entidad a dominio con el mapper.
     */
    @Override
    public Mono<Role> findById(Long id) {
        return roleReactiveRepository.findById(id.intValue())
                .map(roleMapper::toModel);
    }

    /**
     * Guarda un nuevo role en la base de datos.
     * - Convierte de dominio a entidad.
     * - Guarda en la BD.
     * - Convierte la entidad guardada nuevamente a dominio.
     */
    @Override
    public Mono<Role> save(Role role) {
        return roleReactiveRepository.save(roleMapper.toEntity(role))
                .map(roleMapper::toModel);
    }

    /**
     * Actualiza un role existente.
     * - Primero busca si el role existe.
     * - Si existe, convierte el nuevo estado a entidad pero conserva el ID original.
     * - Guarda los cambios y retorna el role actualizado en modelo de dominio.
     */
    @Override
    public Mono<Role> update(Role role) {
        return roleReactiveRepository.findById(role.getUniqueId())
                .flatMap(existing -> {
                    RoleEntity entity = roleMapper.toEntity(role);
                    entity.setIdRole(existing.getIdRole()); // preserva el ID original para no crear uno nuevo
                    return roleReactiveRepository.save(entity);
                })
                .map(roleMapper::toModel);
    }

    /**
     * Elimina un rol por ID.
     * - Convierte el ID de Long a Integer porque la entidad lo maneja así.
     */
    @Override
    public Mono<Void> delete(Long id) {
        return roleReactiveRepository.deleteById(id.intValue());
    }
}
