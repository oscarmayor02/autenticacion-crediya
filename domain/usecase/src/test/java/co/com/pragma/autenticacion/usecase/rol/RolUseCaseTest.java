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

class RolUseCaseTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolUseCase rolUseCase;

    private Rol rol;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        rol = new Rol();
        rol.setUniqueId(1);
        rol.setNombre("ADMIN");
    }

    @Test
    void crearRol_ok() {
        when(rolRepository.save(rol)).thenReturn(Mono.just(rol));

        StepVerifier.create(rolUseCase.crearRol(rol))
                .expectNext(rol)
                .verifyComplete();
    }

    @Test
    void obtenerRol_ok() {
        when(rolRepository.findById(1L)).thenReturn(Mono.just(rol));

        StepVerifier.create(rolUseCase.obtenerRolPorId(1L))
                .expectNext(rol)
                .verifyComplete();
    }

    @Test
    void actualizarRol_ok() {
        when(rolRepository.update(rol)).thenReturn(Mono.just(rol));

        StepVerifier.create(rolUseCase.actualizarRol(rol))
                .expectNext(rol)
                .verifyComplete();
    }

    @Test
    void eliminarRol_ok() {
        when(rolRepository.delete(1L)).thenReturn(Mono.empty());

        StepVerifier.create(rolUseCase.eliminarRol(1L))
                .verifyComplete();
    }
}