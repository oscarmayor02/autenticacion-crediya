package co.com.pragma.autenticacion.usecase.rol;

import co.com.pragma.autenticacion.model.role.Role;
import co.com.pragma.autenticacion.model.role.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Caso de uso (lógica de negocio) para manejar ROL.
 *
 * - Usa programación reactiva (Mono) porque se asume que el acceso
 *   al repositorio es NO bloqueante (reactivo).
 */
@RequiredArgsConstructor
public class RoleUseCase {

    // 👉 Inyección del repositorio (implementación viene en infraestructura)
    private final RoleRepository roleRepository;

    // ---------------------- READ ----------------------
    public Mono<Role> getRoleById(Long id) {
        // Devuelve un Mono con el Role o Mono.empty si no existe
        return roleRepository.findById(id);
    }

    // ---------------------- CREATE ----------------------
    public Mono<Role> createRole(Role role) {
        return roleRepository.save(role);
    }

    // ---------------------- UPDATE ----------------------
    public Mono<Role> updateRole(Role role) {
        return roleRepository.update(role);
    }

    // ---------------------- DELETE ----------------------
    public Mono<Void> deleteRole(Long id) {
        return roleRepository.delete(id);
    }
}
