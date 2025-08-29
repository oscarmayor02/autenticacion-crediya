package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.dto.RoleDTO;
import co.com.pragma.autenticacion.model.role.Role;
import co.com.pragma.autenticacion.usecase.rol.RoleUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;@WebFluxTest
@ContextConfiguration(classes = {RouterRestRole.class, HandlerRole.class})
class HandlerRoleTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RoleUseCase roleUseCase;


    @Test
    void crearRol_success() {
        Role role = Role.builder().uniqueId(1).name("ADMIN").descripcion("Role de prueba").build();
        when(roleUseCase.createRole(any(Role.class))).thenReturn(Mono.just(role));

        RoleDTO dto = new RoleDTO();
        dto.setNombre("ADMIN");
        dto.setDescripcion("Role de prueba");

        webTestClient.post()
                .uri("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.nombre").isEqualTo("ADMIN")
                .jsonPath("$.descripcion").isEqualTo("Role de prueba");
    }

    @Test
    void crearRol_validationError() {
        RoleDTO dto = new RoleDTO(); // nombre null => error de validación

        webTestClient.post()
                .uri("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists();
    }
}
