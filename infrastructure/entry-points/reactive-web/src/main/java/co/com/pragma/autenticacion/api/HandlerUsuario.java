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

    private final UserUseCase userUseCase; // Caso de uso de usuario
    private final UserApiMapper userApiMapper; // Mapper DTO ↔ Dominio
    private final Validator validator; // Bean de validación de jakarta
    private final PasswordEncoderPort passwordEncoder; // NUEVO

    /**
     * Registrar usuario
     */
    public Mono<ServerResponse> registerUser(ServerRequest request) {

        return request.bodyToMono(UserRequestDTO.class)
                .flatMap(dto -> {
                    // Validaciones básicas del DTO
                    var violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        String errs = violations.stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.joining(", "));
                        return Mono.error(new ValidationException(errs));
                    }

                    // Validar si email ya existe
                    return userUseCase.existsByEmail(dto.getEmail())
                            .flatMap(emailExists -> {
                                if (emailExists) {
                                    return Mono.error(new ValidationException(AuthConstants.MSG_DUPLICATE_EMAIL));
                                }
                                return Mono.just(dto);
                            });
                })
                .flatMap(dto -> {
                    // Validar si documento ya existe
                    return userUseCase.existsByDocument(dto.getIdentityDocument())
                            .flatMap(docExists -> {
                                if (docExists) {
                                    return Mono.error(new ValidationException(AuthConstants.MSG_DUPLICATE_DOCUMENT));
                                }
                                return Mono.just(dto);
                            });
                })
                // Hash de password ANTES de mapear a dominio
                .flatMap(dto -> passwordEncoder.encode(dto.getPassword())
                        .map(hash -> {
                            dto.setPassword(hash); // reemplaza por hash
                            return dto;
                        })
                )
                .map(userApiMapper::toDomain)
                .flatMap(userUseCase::saveUser)
                .flatMap(savedUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userApiMapper.toResponseDTO(savedUser)))
                .doOnSuccess(u -> log.info(AuthConstants.MGS_USER_CREATE_OK, u))
                .doOnError(e -> log.error(AuthConstants.MSG_INVALID_CREATE_USER, e.getMessage()))
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
                .onErrorResume(NotFoundException.class, e -> ServerResponse
                        .notFound().build());
    }

    /**
     * Editar usuario
     */
    public Mono<ServerResponse> editUser(ServerRequest request) {
        return request.bodyToMono(UserRequestDTO.class)
                .map(userApiMapper::toDomain)
                .flatMap(userUseCase::editUser)
                .map(userApiMapper::toDTO)
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
