package co.com.pragma.autenticacion.usecase.user;

import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.model.user.gateways.UserRepository;
import co.com.pragma.autenticacion.usecase.exceptions.DuplicateException;
import co.com.pragma.autenticacion.usecase.exceptions.NotFoundException;
import co.com.pragma.autenticacion.usecase.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class UserUseCaseTest {

    @Mock
    private UserRepository userRepository; // 👉 Se "falsifica" el repo (no toca BD real)

    @InjectMocks
    private UserUseCase userUseCase; // 👉 Aquí probamos SOLO la lógica del UseCase

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 👉 Usuario válido para pruebas
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
        // 👉 Configuramos mocks: correo y documento NO existen
        when(userRepository.existsByEmail(user.getCorreoElectronico())).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumento(user.getDocumentoIdentidad())).thenReturn(Mono.just(false));
        when(userRepository.saveUser(user)).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectNext(user) // 👉 Esperamos que retorne el mismo usuario
                .verifyComplete();

        verify(userRepository).saveUser(user); // 👉 Verificamos que realmente llamó al repo
    }

    @Test
    void saveUser_emailDuplicado() {
        when(userRepository.existsByEmail(user.getCorreoElectronico())).thenReturn(Mono.just(true));
        when(userRepository.existsByDocumento(user.getDocumentoIdentidad())).thenReturn(Mono.just(false));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectError(DuplicateException.class) // 👉 Debe fallar con excepción de duplicado
                .verify();
    }

    @Test
    void getUserById_existe() {
        when(userRepository.getUserByIdNumber(1L)).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.getUserByIdNumber(1L))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void getUserById_noExiste() {
        when(userRepository.getUserByIdNumber(1L)).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.getUserByIdNumber(1L))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void editUser_ok() {
        when(userRepository.getUserByIdNumber(1L)).thenReturn(Mono.just(user));
        when(userRepository.editUser(user)).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.editUser(user))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void deleteUser_ok() {
        when(userRepository.getUserByIdNumber(1L)).thenReturn(Mono.just(user));
        when(userRepository.deleteUser(1L)).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.deleteUser(1L))
                .verifyComplete();
    }


}