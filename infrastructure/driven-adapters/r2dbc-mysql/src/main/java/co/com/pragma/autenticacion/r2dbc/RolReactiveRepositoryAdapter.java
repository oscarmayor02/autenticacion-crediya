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
    private final RolReactiveRepository repository;

    protected RolReactiveRepositoryAdapter(RolReactiveRepository repository, RolMapper rolMapper) {
        super(repository, null, rolMapper::toModel); // null porque no usamos ReactiveCommons
        this.repository = repository;
        this.rolMapper = rolMapper;
    }

    @Override
    public Mono<Rol> findById(Long id) {
        return repository.findById(id.intValue())
                .map(rolMapper::toModel);
    }

    @Override
    public Mono<Rol> save(Rol rol) {
        return repository.save(rolMapper.toEntity(rol))
                .map(rolMapper::toModel);
    }
}