package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.dto.RoleDTO;
import co.com.pragma.autenticacion.model.role.Role;
import co.com.pragma.autenticacion.usecase.rol.RoleUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

class HandlerRoleTest {

    private WebTestClient webTestClient;
    private RoleUseCase roleUseCase;
    private Validator validator;

    @BeforeEach
    void setup() {
        // Inicializamos los mocks
        roleUseCase = Mockito.mock(RoleUseCase.class);
        validator = Mockito.mock(Validator.class);

        // Creamos el handler real con los mocks
        HandlerRole handlerRole = new HandlerRole(roleUseCase, validator);

        // Router mínimo para pruebas
        RouterFunction<ServerResponse> router = route()
                .POST("/api/v1/roles", handlerRole::crearRol)
                .build();

        // Ligamos WebTestClient al router
        webTestClient = WebTestClient.bindToRouterFunction(router).build();
    }

    /** Helper para crear un DTO de rol de prueba */
    private RoleDTO createTestRoleDTO() {
        RoleDTO dto = new RoleDTO();
        dto.setNombre("ADMIN");
        dto.setDescripcion("Role de prueba");
        return dto;
    }

    /** Test exitoso de creación de rol */
    @Test
    void crearRol_success() {
        RoleDTO dto = createTestRoleDTO();

        // Creamos un Role simulado que debe devolver el use case
        Role role = Role.builder()
                .uniqueId(1)
                .name(dto.getNombre())
                .description(dto.getDescripcion())
                .build();

        // Mock del comportamiento del use case
        when(roleUseCase.createRole(any(Role.class))).thenReturn(Mono.just(role));

        // Mock validator: no hay errores de validación
        when(validator.validate(any(RoleDTO.class))).thenReturn(java.util.Collections.emptySet());

        // Ejecutamos la prueba
        webTestClient.post()
                .uri("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated() // Esperamos 201 Created
                .expectBody()
                .jsonPath("$.name").isEqualTo("ADMIN")
                .jsonPath("$.description").isEqualTo("Role de prueba");
    }

    /** Test de validación: DTO vacío */
    @Test
    void crearRol_validationError() {
        RoleDTO dto = new RoleDTO(); // DTO vacío

        // Mock validator: devuelve error de validación
        jakarta.validation.ConstraintViolation<RoleDTO> violation = Mockito.mock(jakarta.validation.ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Campo requerido");
        when(validator.validate(any(RoleDTO.class))).thenReturn(java.util.Set.of(violation));

        // Ejecutamos la prueba
        webTestClient.post()
                .uri("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest() // Esperamos 400 BAD_REQUEST
                .expectBody()
                .jsonPath("$.error").exists();
    }
}
