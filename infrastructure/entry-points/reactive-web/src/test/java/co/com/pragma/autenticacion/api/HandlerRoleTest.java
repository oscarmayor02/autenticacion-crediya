package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.dto.RolDTO;
import co.com.pragma.autenticacion.model.rol.Rol;
import co.com.pragma.autenticacion.usecase.rol.RolUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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
    private RolUseCase rolUseCase;


    @Test
    void crearRol_success() {
        Rol rol = Rol.builder().uniqueId(1).nombre("ADMIN").descripcion("Rol de prueba").build();
        when(rolUseCase.crearRol(any(Rol.class))).thenReturn(Mono.just(rol));

        RolDTO dto = new RolDTO();
        dto.setNombre("ADMIN");
        dto.setDescripcion("Rol de prueba");

        webTestClient.post()
                .uri("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.nombre").isEqualTo("ADMIN")
                .jsonPath("$.descripcion").isEqualTo("Rol de prueba");
    }

    @Test
    void crearRol_validationError() {
        RolDTO dto = new RolDTO(); // nombre null => error de validación

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
