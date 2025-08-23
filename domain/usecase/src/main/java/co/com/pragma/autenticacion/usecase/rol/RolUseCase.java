package co.com.pragma.autenticacion.usecase.rol;

import co.com.pragma.autenticacion.model.rol.Rol;
import co.com.pragma.autenticacion.model.rol.gateways.RolRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RolUseCase {
    private final RolRepository rolRepository;

    public Mono<Rol> obtenerRolPorId(Long id) {
        return rolRepository.findById(id);
    }

    public Mono<Rol> crearRol(Rol rol) {
        return rolRepository.save(rol);
    }
}
