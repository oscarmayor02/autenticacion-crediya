package co.com.pragma.autenticacion.usecase.rol;

import co.com.pragma.autenticacion.model.rol.Rol;
import co.com.pragma.autenticacion.model.rol.gateways.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

// Test unitario para probar la lógica del RolUseCase sin tocar la BD real.
class RolUseCaseTest {

    @Mock
    private RolRepository rolRepository; //  Mock: simulamos el repositorio (no conecta a DB).

    @InjectMocks
    private RolUseCase rolUseCase; //  Clase que probamos (se inyecta el mock del repo).

    private Rol rol; //  Objeto que usaremos en pruebas.

    @BeforeEach
    void setUp() {
        //  Inicializa los mocks de Mockito antes de cada test.
        MockitoAnnotations.openMocks(this);

        //  Creamos un rol de ejemplo para pruebas.
        rol = new Rol();
        rol.setUniqueId(1);
        rol.setNombre("ADMIN");
    }

    @Test
    void crearRol_ok() {
        //  Simulamos que el repo guarda el rol y retorna el mismo objeto.
        when(rolRepository.save(rol)).thenReturn(Mono.just(rol));

        //  Verificamos con StepVerifier que el flujo retorna ese rol.
        StepVerifier.create(rolUseCase.crearRol(rol))
                .expectNext(rol) //  Esperamos que salga el mismo rol
                .verifyComplete(); //  Y que finalice sin error.
    }

    @Test
    void obtenerRol_ok() {
        //  Simulamos que el repo encuentra el rol por ID.
        when(rolRepository.findById(1L)).thenReturn(Mono.just(rol));

        StepVerifier.create(rolUseCase.obtenerRolPorId(1L))
                .expectNext(rol) //  Esperamos ese rol
                .verifyComplete();
    }

    @Test
    void actualizarRol_ok() {
        //  Simulamos actualización (repo retorna el mismo rol actualizado).
        when(rolRepository.update(rol)).thenReturn(Mono.just(rol));

        StepVerifier.create(rolUseCase.actualizarRol(rol))
                .expectNext(rol)
                .verifyComplete();
    }

    @Test
    void eliminarRol_ok() {
        //  Simulamos que al eliminar retorna vacío (Mono.empty()).
        when(rolRepository.delete(1L)).thenReturn(Mono.empty());

        StepVerifier.create(rolUseCase.eliminarRol(1L))
                .verifyComplete(); //  Esperamos que termine sin error.
    }
}
