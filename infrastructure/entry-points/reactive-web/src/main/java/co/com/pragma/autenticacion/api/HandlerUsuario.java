package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.dto.UsuarioRequestDTO;
import co.com.pragma.autenticacion.api.mapper.UsuarioApiMapper;
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
import reactor.core.publisher.Mono;

import java.util.List;
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
                .map(dto -> {
                    log.info("Petición para registrar usuario: {}", dto.getEmail());
                    var violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        List<String> errs = violations.stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.toList());
                        throw new ValidationException(errs);
                    }
                    return usuarioMapper.toDomain(dto);
                })
                .flatMap(usuario -> {
                    return userUseCase.saveUser(usuario);
                }) // devuelve Mono<User>
                .flatMap(savedUsuario -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUsuario)) // aquí cambia el tipo final a Mono<ServerResponse>
                .onErrorResume(e -> {
                    log.error("Error registrando usuario: {}", e.getMessage());
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue("{\"error\":\"" + e.getMessage() + "\"}");
                });
    }



}
