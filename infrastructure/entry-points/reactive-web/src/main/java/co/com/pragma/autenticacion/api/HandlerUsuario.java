package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.dto.UsuarioRequestDTO;
import co.com.pragma.autenticacion.api.mapper.UsuarioApiMapper;
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
    private final UserUseCase userUseCase;
    private final UsuarioApiMapper usuarioMapper;
    private final Validator validator; // Bean de jakarta validation

    public Mono<ServerResponse> registrarUsuario(ServerRequest request) {
        return request.bodyToMono(UsuarioRequestDTO.class)
                .flatMap(dto -> {
                    // 1️⃣ Validaciones básicas
                    var violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        String errs = violations.stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.joining(", "));
                        return Mono.error(new ValidationException(errs));
                    }

                    // 2️⃣ Validación de email único
                    return userUseCase.existsByEmail(dto.getEmail())
                            .flatMap(emailExists -> {
                                if (emailExists) {
                                    return Mono.error(new ValidationException("El correo electrónico ya está registrado"));
                                }
                                return Mono.just(dto);
                            });
                })
                .flatMap(dto -> {
                    // 3️⃣ Validación de documento único
                    return userUseCase.existsByDocumento(dto.getDocumentoIdentidad())
                            .flatMap(docExists -> {
                                if (docExists) {
                                    return Mono.error(new ValidationException("El documento de identidad ya está registrado"));
                                }
                                return Mono.just(dto);
                            });
                })
                .map(usuarioMapper::toDomain)
                .flatMap(userUseCase::saveUser)
                .flatMap(savedUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(usuarioMapper.toDTO(savedUser)))
                .onErrorResume(e -> {
                    if (e instanceof ValidationException || e instanceof DuplicateException) {
                        log.warn("Error registrando usuario", e);

                        return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("{\"error\":\"" + e.getMessage() + "\"}");
                    }
                    return ServerResponse.status(500)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue("{\"error\":\"Error interno del servidor\"}");
                });
    }


    // Obtener todos los usuarios
    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        Flux<UsuarioRequestDTO> users = userUseCase.getAllUsers()
                .map(usuarioMapper::toDTO);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(users, UsuarioRequestDTO.class);
    }

    // Obtener usuario por ID
    public Mono<ServerResponse> getUserById(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return userUseCase.getUserByIdNumber(id)
                .map(usuarioMapper::toDTO)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .onErrorResume(NotFoundException.class,
                        e -> ServerResponse.notFound().build());
    }

    // Editar usuario
    public Mono<ServerResponse> editUser(ServerRequest request) {
        return request.bodyToMono(UsuarioRequestDTO.class)
                .map(usuarioMapper::toDomain)
                .flatMap(userUseCase::editUser)
                .map(usuarioMapper::toDTO)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .onErrorResume(NotFoundException.class,
                        e -> ServerResponse.notFound().build());
    }

    // Eliminar usuario
    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return userUseCase.deleteUser(id)
                .then(ServerResponse.noContent().build())
                .onErrorResume(NotFoundException.class,
                        e -> ServerResponse.notFound().build());
    }

    // Verificar si email existe
    public Mono<ServerResponse> existsByEmail(ServerRequest request) {
        String email = request.pathVariable("email");
        return userUseCase.existsByEmail(email)
                .flatMap(exists -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("{\"exists\": " + exists + "}"));
    }

    // Verificar si documento existe
    public Mono<ServerResponse> existsByDocumento(ServerRequest request) {
        String doc = request.pathVariable("documento");
        return userUseCase.existsByDocumento(doc)
                .flatMap(exists -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("{\"exists\": " + exists + "}"));
    }
}

