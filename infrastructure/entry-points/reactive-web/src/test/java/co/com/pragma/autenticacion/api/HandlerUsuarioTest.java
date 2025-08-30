package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.dto.UserRequestDTO;
import co.com.pragma.autenticacion.api.mapper.UserApiMapper;
import co.com.pragma.autenticacion.model.auth.gateways.PasswordEncoderPort;
import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.usecase.user.UserUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

class HandlerUsuarioTest {

    private WebTestClient webTestClient;
    private UserUseCase userUseCase;
    private UserApiMapper usuarioMapper;
    private Validator validator;
    private PasswordEncoderPort passwordEncoder;

    @BeforeEach
    void setup() {
        userUseCase = mock(UserUseCase.class);
        usuarioMapper = mock(UserApiMapper.class);
        validator = mock(Validator.class);
        passwordEncoder = mock(PasswordEncoderPort.class);

        // Simula que el PasswordEncoder siempre devuelve un hash
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation ->
                Mono.just("hashed-" + invocation.getArgument(0))
        );

        // Mapper seguro: convierte DTO -> Domain
        when(usuarioMapper.toDomain(any(UserRequestDTO.class))).thenAnswer(invocation -> {
            UserRequestDTO dto = invocation.getArgument(0);
            return User.builder()
                    .name(dto.getName())
                    .lastName(dto.getLastName())
                    .email(dto.getEmail())
                    .identityDocument(dto.getIdentityDocument())
                    .dateOfBirth(dto.getDateOfBirth())
                    .telephone(dto.getTelephone())
                    .baseSalary(dto.getBaseSalary() != null ? BigDecimal.valueOf(dto.getBaseSalary()) : null)
                    .address(dto.getAddress())
                    .idRole(dto.getRoleId() != null ? BigDecimal.valueOf(dto.getRoleId()) : null)
                    .password(dto.getPassword())
                    .build();
        });

        // Mapper seguro: Domain -> DTO
        when(usuarioMapper.toDTO(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserRequestDTO dto = new UserRequestDTO();
            dto.setName(user.getName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
            dto.setIdentityDocument(user.getIdentityDocument());
            dto.setDateOfBirth(user.getDateOfBirth());
            dto.setTelephone(user.getTelephone());
            dto.setBaseSalary(user.getBaseSalary() != null ? user.getBaseSalary().doubleValue() : null);
            dto.setAddress(user.getAddress());
            dto.setRoleId(user.getIdRole() != null ? user.getIdRole().intValue() : null);
            dto.setPassword(user.getPassword());
            return dto;
        });

        // Handler real con los mocks
        HandlerUsuario handler = new HandlerUsuario(userUseCase, usuarioMapper, validator, passwordEncoder);

        // Router mínimo para pruebas
        RouterFunction<ServerResponse> router = route()
                .POST("/api/v1/usuarios", handler::registerUser)
                .build();

        webTestClient = WebTestClient.bindToRouterFunction(router).build();
    }

    // Helper: crear un DTO de prueba
    private UserRequestDTO createTestUserDTO() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("Juan");
        dto.setLastName("Perez");
        dto.setEmail("juan@example.com");
        dto.setIdentityDocument("12345678");
        dto.setDateOfBirth("1990-01-01");
        dto.setTelephone("3001234567");
        dto.setBaseSalary(2000.0);
        dto.setAddress("Calle 1");
        dto.setRoleId(1);
        dto.setPassword("123456"); // obligatorio
        return dto;
    }

    // Helper: crear el User de dominio correspondiente
    private User createTestUserDomain(UserRequestDTO dto) {
        return User.builder()
                .name(dto.getName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .identityDocument(dto.getIdentityDocument())
                .dateOfBirth(dto.getDateOfBirth())
                .telephone(dto.getTelephone())
                .baseSalary(BigDecimal.valueOf(dto.getBaseSalary()))
                .address(dto.getAddress())
                .idRole(BigDecimal.valueOf(dto.getRoleId()))
                .password(dto.getPassword())
                .build();
    }



    @Test
    void userRegister_validationError() {
        UserRequestDTO dto = new UserRequestDTO(); // DTO vacío

        // Simula error de validación
        ConstraintViolation<UserRequestDTO> violation = mock(ConstraintViolation.class);
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
