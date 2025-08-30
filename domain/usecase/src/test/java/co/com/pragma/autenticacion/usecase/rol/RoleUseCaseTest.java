package co.com.pragma.autenticacion.usecase.rol;

import co.com.pragma.autenticacion.model.role.Role;
import co.com.pragma.autenticacion.model.role.gateways.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

// Test unitario para probar la lógica del RoleUseCase sin tocar la BD real.
class RoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository; //  Mock: simulamos el repositorio (no conecta a DB).

    @InjectMocks
    private RoleUseCase roleUseCase; //  Clase que probamos (se inyecta el mock del repo).

    private Role role; //  Objeto que usaremos en pruebas.

    @BeforeEach
    void setUp() {
        //  Inicializa los mocks de Mockito antes de cada test.
        MockitoAnnotations.openMocks(this);

        //  Creamos un role de ejemplo para pruebas.
        role = new Role();
        role.setUniqueId(1);
        role.setName("ADMIN");
    }

    @Test
    void createRole_ok() {
        //  Simulamos que el repo guarda el role y retorna el mismo objeto.
        when(roleRepository.save(role)).thenReturn(Mono.just(role));

        //  Verificamos con StepVerifier que el flujo retorna ese role.
        StepVerifier.create(roleUseCase.createRole(role))
                .expectNext(role) //  Esperamos que salga el mismo role
                .verifyComplete(); //  Y que finalice sin error.
    }

    @Test
    void obtenerRol_ok() {
        //  Simulamos que el repo encuentra el role por ID.
        when(roleRepository.findById(1L)).thenReturn(Mono.just(role));

        StepVerifier.create(roleUseCase.getRoleById(1L))
                .expectNext(role) //  Esperamos ese role
                .verifyComplete();
    }

    @Test
    void updateRole_ok() {
        //  Simulamos actualización (repo retorna el mismo role actualizado).
        when(roleRepository.update(role)).thenReturn(Mono.just(role));

        StepVerifier.create(roleUseCase.updateRole(role))
                .expectNext(role)
                .verifyComplete();
    }

    @Test
    void deleteRole_ok() {
        //  Simulamos que al eliminar retorna vacío (Mono.empty()).
        when(roleRepository.delete(1L)).thenReturn(Mono.empty());

        StepVerifier.create(roleUseCase.deleteRole(1L))
                .verifyComplete(); //  Esperamos que termine sin error.
    }
}
