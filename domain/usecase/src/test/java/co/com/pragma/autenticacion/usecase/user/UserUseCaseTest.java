package co.com.pragma.autenticacion.usecase.user;

import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.model.user.gateways.UserRepository;
import co.com.pragma.autenticacion.usecase.exceptions.DuplicateException;
import co.com.pragma.autenticacion.usecase.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

// Test unitario para la lógica del UserUseCase con mocks del repositorio.
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository; //  Mock: simulamos el acceso a la BD.

    @InjectMocks
    private UserUseCase userUseCase; //Clase que vamos a probar.

    private User user; //  Usuario de ejemplo para pruebas.

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks.

        //  Instanciamos un usuario válido
        user = new User();
        user.setIdNumber(1L);
        user.setNombre("Oscar");
        user.setApellido("Mayor");
        user.setCorreoElectronico("oscar@test.com");
        user.setDocumentoIdentidad("12345678");
        user.setSalarioBase(new BigDecimal("2000000"));
        user.setFechaNacimiento("1990-05-10");
    }

    @Test
    void saveUser_ok() {
        //  Simulamos que NO existen duplicados
        when(userRepository.existsByEmail(user.getCorreoElectronico())).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumento(user.getDocumentoIdentidad())).thenReturn(Mono.just(false));

        //  Simulamos que el repo guarda el usuario
        when(userRepository.saveUser(user)).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectNext(user) //  Esperamos que retorne el usuario
                .verifyComplete();

        //  Validamos que efectivamente se llamó al saveUser del repo
        verify(userRepository).saveUser(user);
    }

    @Test
    void saveUser_emailDuplicado() {
        //  Simulamos que el correo ya existe
        when(userRepository.existsByEmail(user.getCorreoElectronico())).thenReturn(Mono.just(true));
        when(userRepository.existsByDocumento(user.getDocumentoIdentidad())).thenReturn(Mono.just(false));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectError(DuplicateException.class) //  Debe lanzar excepción
                .verify();
    }

    @Test
    void getUserById_existe() {
        //  Simulamos que el usuario existe
        when(userRepository.getUserByIdNumber(1L)).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.getUserByIdNumber(1L))
                .expectNext(user) //  Retorna usuario
                .verifyComplete();
    }

    @Test
    void getUserById_noExiste() {
        //  Simulamos que el usuario NO existe
        when(userRepository.getUserByIdNumber(1L)).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.getUserByIdNumber(1L))
                .expectError(NotFoundException.class) //  Debe lanzar excepción
                .verify();
    }

    @Test
    void editUser_ok() {
        //  Simulamos que el usuario existe y luego lo actualiza
        when(userRepository.getUserByIdNumber(1L)).thenReturn(Mono.just(user));
        when(userRepository.editUser(user)).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.editUser(user))
                .expectNext(user) //  Usuario actualizado
                .verifyComplete();
    }

    @Test
    void deleteUser_ok() {
        //  Simulamos que el usuario existe y se elimina
        when(userRepository.getUserByIdNumber(1L)).thenReturn(Mono.just(user));
        when(userRepository.deleteUser(1L)).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.deleteUser(1L))
                .verifyComplete(); //  Terminó bien
    }
}
