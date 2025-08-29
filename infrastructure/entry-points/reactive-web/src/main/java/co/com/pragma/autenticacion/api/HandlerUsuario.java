package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.dto.UserRequestDTO;
import co.com.pragma.autenticacion.api.mapper.UserApiMapper;
import co.com.pragma.autenticacion.model.auth.AuthConstants;
import co.com.pragma.autenticacion.model.auth.gateways.PasswordEncoderPort;
import co.com.pragma.autenticacion.usecase.exceptions.DuplicateException;
import co.com.pragma.autenticacion.usecase.exceptions.NotFoundException;
import co.com.pragma.autenticacion.usecase.exceptions.ValidationException;
import co.com.pragma.autenticacion.usecase.user.UserUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HandlerUsuario {

    private static final Logger log = LoggerFactory.getLogger(HandlerUsuario.class);

    private final UserUseCase userUseCase;         // Caso de uso de usuario
    private final UserApiMapper userApiMapper;     // Mapper DTO ↔ Dominio
    private final Validator validator;             // Bean de validación de Jakarta
    private final PasswordEncoderPort passwordEncoder; // Puerto para hashear password

    /**
     * Registrar usuario
     */
    public Mono<ServerResponse> registerUser(ServerRequest request) {

        // Paso 1: Leer el body del request como DTO
        return request.bodyToMono(UserRequestDTO.class)

                // Paso 2: Validar campos con Jakarta Validator
                .flatMap(dto -> {
                    var violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        String errs = violations.stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.joining(", "));
                        return Mono.error(new ValidationException(errs));
                    }
                    return Mono.just(dto);
                })

                // Paso 3: Validar email existente usando filterWhen
                .filterWhen(dto -> userUseCase.existsByEmail(dto.getEmail())
                        .map(exists -> !exists)) // pasa solo si NO existe
                .switchIfEmpty(Mono.error(new ValidationException(AuthConstants.MSG_DUPLICATE_EMAIL)))

                // Paso 4: Validar documento existente
                .filterWhen(dto -> userUseCase.existsByDocument(dto.getIdentityDocument())
                        .map(exists -> !exists)) // pasa solo si NO existe
                .switchIfEmpty(Mono.error(new ValidationException(AuthConstants.MSG_DUPLICATE_DOCUMENT)))

                // Paso 5: Hashear password de manera reactiva
                .flatMap(dto -> passwordEncoder.encode(dto.getPassword())
                        .map(hash -> {
                            dto.setPassword(hash);
                            return dto;
                        })
                )

                // Paso 6: Mapear DTO a modelo de dominio
                .map(userApiMapper::toDomain)

                // Paso 7: Guardar usuario usando caso de uso
                .flatMap(userUseCase::saveUser)

                // Paso 8: Mapear de vuelta a DTO para la respuesta
                .map(userApiMapper::toResponseDTO)

                // Paso 9: Construir respuesta HTTP
                .flatMap(savedDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedDto))

                // Logging de éxito
                .doOnSuccess(u -> log.info(AuthConstants.MGS_USER_CREATE_OK, u))

                // Logging de error
                .doOnError(e -> log.error(AuthConstants.MSG_INVALID_CREATE_USER, e.getMessage()))

                // Manejo de errores finales
                .onErrorResume(e -> {
                    if (e instanceof ValidationException || e instanceof DuplicateException) {
                        return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("{\"error\":\"" + e.getMessage() + "\"}");
                    }
                    return ServerResponse.status(500)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue("{\"error\":\"Error interno del servidor\"}");
                });
    }

    /**
     * Obtener todos los usuarios
     */
    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        // Flux de usuarios mapeado a DTO
        Flux<UserRequestDTO> users = userUseCase.getAllUsers()
                .map(userApiMapper::toDTO);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(users, UserRequestDTO.class)
                .doOnSuccess(u -> log.info(AuthConstants.MSG_USER_GET_OK))
                .doOnError(e -> log.error(AuthConstants.MSG_INVALID_GET_USER, e.getMessage()));
    }

    /**
     * Obtener usuario por ID
     */
    public Mono<ServerResponse> getUserById(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        log.info("Consultando usuario con id: {}", id);

        return userUseCase.getUserByIdNumber(id)
                .map(userApiMapper::toDTO)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .onErrorResume(NotFoundException.class, e -> ServerResponse.notFound().build());
    }

    /**
     * Editar usuario
     */
    public Mono<ServerResponse> editUser(ServerRequest request) {
        return request.bodyToMono(UserRequestDTO.class)
                .map(userApiMapper::toDomain)           // Mapear DTO → dominio
                .flatMap(userUseCase::editUser)         // Editar usuario reactivo
                .map(userApiMapper::toDTO)              // Mapear dominio → DTO
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .doOnSuccess(u -> log.info(AuthConstants.MSG_USER_UPDATE_OK, u))
                .doOnError(e -> log.error(AuthConstants.MSG_INVALID_EDIT_USER, e.getMessage()))
                .onErrorResume(NotFoundException.class, e -> ServerResponse.notFound().build());
    }

    /**
     * Eliminar usuario
     */
    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        log.info("Eliminando usuario con id: {}", id);

        return userUseCase.deleteUser(id)
                .then(ServerResponse.noContent().build())
                .doOnSuccess(u -> log.info(AuthConstants.MSG_USER_DELETE_OK, id))
                .doOnError(e -> log.error(AuthConstants.MSG_INVALID_DELETE_USER, e.getMessage()))
                .onErrorResume(NotFoundException.class, e -> ServerResponse.notFound().build());
    }

    /**
     * Verificar si email existe
     */
    public Mono<ServerResponse> existsByEmail(ServerRequest request) {
        String email = request.pathVariable("email");

        return userUseCase.existsByEmail(email)
                .flatMap(exists -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("{\"exists\": " + exists + "}"))
                .doOnError(e -> log.error(AuthConstants.VALIDATION_EMAIL, e.getMessage()));
    }

    /**
     * Verificar si documento existe
     */
    public Mono<ServerResponse> existsByDocument(ServerRequest request) {
        String doc = request.pathVariable("documento");

        return userUseCase.existsByDocument(doc)
                .flatMap(exists -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("{\"exists\": " + exists + "}"))
                .doOnError(e -> log.error(AuthConstants.VALIDATION_DOCUMENT_INVALID, e.getMessage()));
    }
}
