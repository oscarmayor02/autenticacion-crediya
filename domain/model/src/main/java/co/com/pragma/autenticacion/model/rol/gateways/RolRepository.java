package co.com.pragma.autenticacion.model.rol.gateways;

import co.com.pragma.autenticacion.model.rol.Rol;
import reactor.core.publisher.Mono;

public interface RolRepository {
    Mono<Rol> findById(Long id);
    Mono<Rol> save(Rol rol);
    Mono<Rol> update(Rol rol);         // Actualizar rol existente
    Mono<Void> delete(Long id);
}
