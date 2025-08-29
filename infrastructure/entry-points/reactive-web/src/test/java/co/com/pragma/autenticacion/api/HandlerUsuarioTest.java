package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.dto.UserRequestDTO;
import co.com.pragma.autenticacion.api.mapper.UserApiMapper;
import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.usecase.user.UserUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

class HandlerUsuarioTest {

    private WebTestClient webTestClient;
    private UserUseCase userUseCase;
    private UserApiMapper usuarioMapper;
    private Validator validator;

    @BeforeEach
    void setup() {
        // Mocks
        userUseCase = mock(UserUseCase.class);
        usuarioMapper = mock(UserApiMapper.class);
        validator = mock(Validator.class);

        // Handler real con mocks
        HandlerUsuario handler = new HandlerUsuario(userUseCase, usuarioMapper, validator);

        // Router mínimo para pruebas
        RouterFunction<ServerResponse> router = route()
                .POST("/api/v1/usuarios", handler::registerUser)
                .build();

        // WebTestClient ligado al router
        webTestClient = WebTestClient.bindToRouterFunction(router).build();
    }

    @Test
    void userRegister_success() {
        // DTO de prueba
        UserRequestDTO dto = new UserRequestDTO();
        dto.setNombre("Juan");
        dto.setApellido("Perez");
        dto.setEmail("juan@example.com");
        dto.setDocumentoIdentidad("12345678");

        // Domain User
        User user = new User();
        user.setEmail("juan@example.com");

        // Comportamiento de mocks
        when(validator.validate(dto)).thenReturn(Collections.emptySet());
        when(userUseCase.existsByEmail(dto.getEmail())).thenReturn(Mono.just(false));
        when(userUseCase.existsByDocument(dto.getDocumentoIdentidad())).thenReturn(Mono.just(false));
        when(usuarioMapper.toDomain(dto)).thenReturn(user);
        when(userUseCase.saveUser(user)).thenReturn(Mono.just(user));
        when(usuarioMapper.toDTO(user)).thenReturn(dto);

        // Test
        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.nombre").isEqualTo("Juan")
                .jsonPath("$.apellido").isEqualTo("Perez");
    }

    @Test
    void userRegister_validationError() {
        UserRequestDTO dto = new UserRequestDTO(); // vacío

        // Mock validator para que devuelva error
        var violation = mock(jakarta.validation.ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Campo requerido");
        when(validator.validate(dto)).thenReturn(Set.of(violation));

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists();
    }
}
