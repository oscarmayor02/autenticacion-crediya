package co.com.pragma.autenticacion.r2dbc;

import co.com.pragma.autenticacion.r2dbc.entity.RoleEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolReactiveRepository extends ReactiveCrudRepository<RoleEntity, Integer>,
        ReactiveQueryByExampleExecutor<RoleEntity> {
}