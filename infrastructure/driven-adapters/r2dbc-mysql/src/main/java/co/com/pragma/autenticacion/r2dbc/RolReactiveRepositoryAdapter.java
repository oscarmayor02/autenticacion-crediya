package co.com.pragma.autenticacion.r2dbc;


import co.com.pragma.autenticacion.model.rol.Rol;
import co.com.pragma.autenticacion.model.rol.gateways.RolRepository;
import co.com.pragma.autenticacion.r2dbc.entity.RoleEntity;
import co.com.pragma.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.autenticacion.r2dbc.mapper.RolMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class RolReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<Rol, RoleEntity, Integer, RolReactiveRepository>
        implements RolRepository {

    private final RolMapper rolMapper;
    private final RolReactiveRepository rolReactiveRepository;

    protected RolReactiveRepositoryAdapter(RolReactiveRepository rolReactiveRepository, RolMapper rolMapper) {
        super(rolReactiveRepository, null, rolMapper::toModel); // null porque no usamos ReactiveCommons
        this.rolReactiveRepository = rolReactiveRepository;
        this.rolMapper = rolMapper;
    }

    @Override
    public Mono<Rol> findById(Long id) {
        return rolReactiveRepository.findById(id.intValue())
                .map(rolMapper::toModel);
    }

    @Override
    public Mono<Rol> save(Rol rol) {
        return rolReactiveRepository.save(rolMapper.toEntity(rol))
                .map(rolMapper::toModel);
    }

    @Override
    public Mono<Rol> update(Rol rol) {
        return rolReactiveRepository.findById(rol.getUniqueId())
                .flatMap(existing -> {
                    RoleEntity entity = rolMapper.toEntity(rol);
                    entity.setIdRol(existing.getIdRol()); // conservar ID real
                    return rolReactiveRepository.save(entity);
                })
                .map(rolMapper::toModel);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return rolReactiveRepository.deleteById(id.intValue());
    }
}