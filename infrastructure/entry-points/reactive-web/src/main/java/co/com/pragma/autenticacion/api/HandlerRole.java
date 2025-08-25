package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.dto.RolDTO;
import co.com.pragma.autenticacion.model.rol.Rol;
import co.com.pragma.autenticacion.usecase.exceptions.ValidationException;
import co.com.pragma.autenticacion.usecase.rol.RolUseCase;
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

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HandlerRole {

    private static final Logger log = LoggerFactory.getLogger(HandlerRole.class);

    private final RolUseCase rolUseCase;
    private final Validator validator;

    /**
     * Crear un rol
     */
    public Mono<ServerResponse> crearRol(ServerRequest request) {
        return request.bodyToMono(RolDTO.class)
                .map(dto -> {
                    log.info("Petición para crear rol: {}", dto.getNombre());
                    var violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        String errs = violations.stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.joining(", "));
                        throw new ValidationException(errs);
                    }
                    return Rol.builder()
                            .uniqueId(dto.getUniqueId())
                            .nombre(dto.getNombre())
                            .descripcion(dto.getDescripcion())
                            .build();
                })
                .flatMap(rolUseCase::crearRol)
                .flatMap(savedRol -> ServerResponse.status(201)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedRol))
                .doOnSuccess(r -> log.info("Rol creado exitosamente: {}", r))
                .doOnError(e -> log.error("Error creando rol: {}", e.getMessage()))
                .onErrorResume(e -> ServerResponse.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("{\"error\":\"" + e.getMessage() + "\"}"));
    }

    /**
     * Obtener rol por ID
     */
    public Mono<ServerResponse> obtenerRolPorId(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("uniqueId"));
        log.info("Consultando rol con ID: {}", id);
        return rolUseCase.obtenerRolPorId(id)
                .flatMap(rol -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(rol))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * Actualizar rol
     */
    public Mono<ServerResponse> actualizarRol(ServerRequest request) {
        return request.bodyToMono(RolDTO.class)
                .map(this::validateDto)
                .flatMap(rolUseCase::actualizarRol)
                .flatMap(updatedRol -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updatedRol))
                .doOnSuccess(r -> log.info("Rol actualizado: {}", r))
                .doOnError(e -> log.error("Error actualizando rol: {}", e.getMessage()))
                .onErrorResume(this::handleError);
    }

    /**
     * Eliminar rol
     */
    public Mono<ServerResponse> eliminarRol(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("uniqueId"));
        log.info("Eliminando rol con ID: {}", id);
        return rolUseCase.eliminarRol(id)
                .then(ServerResponse.noContent().build())
                .doOnSuccess(r -> log.info("Rol eliminado con ID: {}", id))
                .doOnError(e -> log.error("Error eliminando rol: {}", e.getMessage()))
                .onErrorResume(this::handleError);
    }

    /**
     * Validar DTO de rol
     */
    private Rol validateDto(RolDTO dto) {
        var violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String errs = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new ValidationException(errs);
        }
        return Rol.builder()
                .uniqueId(dto.getUniqueId())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .build();
    }

    /**
     * Manejo general de errores
     */
    private Mono<ServerResponse> handleError(Throwable e) {
        log.error("Error: {}", e.getMessage());
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"error\":\"" + e.getMessage() + "\"}");
    }
}
