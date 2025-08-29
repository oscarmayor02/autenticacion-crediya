package co.com.pragma.autenticacion.r2dbc;

import co.com.pragma.autenticacion.r2dbc.entity.RoleEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio reactivo para "roles".
 * Extiende ReactiveCrudRepository para operaciones CRUD reactivas.
 */
@Repository
public interface RoleReactiveRepository extends
        ReactiveCrudRepository<RoleEntity, Integer>, // CRUD base
        ReactiveQueryByExampleExecutor<RoleEntity> { // Búsquedas dinámicas
}
