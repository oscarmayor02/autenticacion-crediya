package co.com.pragma.autenticacion.r2dbc;

import co.com.pragma.autenticacion.r2dbc.entity.UserEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

// TODO: This file is just an example, you should delete or modify it
public interface UsuarioReactiveRepository extends ReactiveCrudRepository<UserEntity, Long>, ReactiveQueryByExampleExecutor<UserEntity> {
    Mono<Boolean> existsByCorreoElectronico(String correoElectronico);
    Mono<Boolean> existsByDocumentoIdentidad(String documentoIdentidad);
    Mono<Boolean> existsByRolId(Long rolId);

}

