package co.com.pragma.autenticacion.usecase.rol;

import co.com.pragma.autenticacion.model.rol.Rol;
import co.com.pragma.autenticacion.model.rol.gateways.RolRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Caso de uso (lógica de negocio) para manejar ROL.
 *
 * - Usa programación reactiva (Mono) porque se asume que el acceso
 *   al repositorio es NO bloqueante (reactivo).
 */
@RequiredArgsConstructor
public class RolUseCase {

    // 👉 Inyección del repositorio (implementación viene en infraestructura)
    private final RolRepository rolRepository;

    // ---------------------- READ ----------------------
    public Mono<Rol> obtenerRolPorId(Long id) {
        // Devuelve un Mono con el Rol o Mono.empty si no existe
        return rolRepository.findById(id);
    }

    // ---------------------- CREATE ----------------------
    public Mono<Rol> crearRol(Rol rol) {
        return rolRepository.save(rol);
    }

    // ---------------------- UPDATE ----------------------
    public Mono<Rol> actualizarRol(Rol rol) {
        return rolRepository.update(rol);
    }

    // ---------------------- DELETE ----------------------
    public Mono<Void> eliminarRol(Long id) {
        return rolRepository.delete(id);
    }
}
