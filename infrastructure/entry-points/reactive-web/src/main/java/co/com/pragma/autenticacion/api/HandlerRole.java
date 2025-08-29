package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.dto.RoleDTO;
import co.com.pragma.autenticacion.model.auth.AuthConstants;
import co.com.pragma.autenticacion.model.role.Role;
import co.com.pragma.autenticacion.usecase.exceptions.ValidationException;
import co.com.pragma.autenticacion.usecase.rol.RoleUseCase;
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

    private final RoleUseCase roleUseCase;
    private final Validator validator;

    /**
     * Crear un rol
     */
    public Mono<ServerResponse> crearRol(ServerRequest request) {
        return request.bodyToMono(RoleDTO.class)
                .map(dto -> {
                    log.info("Petición para crear rol: {}", dto.getNombre());
                    var violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        String errs = violations.stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.joining(", "));
                        throw new ValidationException(errs);
                    }
                    return Role.builder()
                            .uniqueId(dto.getUniqueId())
                            .name(dto.getNombre())
                            .description(dto.getDescripcion())
                            .build();
                })
                .flatMap(roleUseCase::createRole)
                .flatMap(savedRol -> ServerResponse.status(201)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedRol))
                .doOnSuccess(r -> log.info(AuthConstants.VALIDATION_ROLE_CREATE, r))
                .doOnError(e -> log.error(AuthConstants.MSG_INVALID_CREATE_ROL, e.getMessage()))
                .onErrorResume(e -> ServerResponse.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("{\"error\":\"" + e.getMessage() + "\"}"));
    }

    /**
     * Obtener rol por ID
     */
    public Mono<ServerResponse> getRoleById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("uniqueId"));
        log.info("Consultando rol con ID: {}", id);
        return roleUseCase.getRoleById(id)
                .flatMap(rol -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(rol))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * Actualizar rol
     */
    public Mono<ServerResponse> updateRole(ServerRequest request) {
        return request.bodyToMono(RoleDTO.class)
                .map(this::validateDto)
                .flatMap(roleUseCase::updateRole)
                .flatMap(updatedRol -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updatedRol))
                .doOnSuccess(r -> log.info("Role actualizado: {}", r))
                .doOnError(e -> log.error("Error actualizando rol: {}", e.getMessage()))
                .onErrorResume(this::handleError);
    }

    /**
     * Eliminar rol
     */
    public Mono<ServerResponse> deleteRole(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("uniqueId"));
        log.info("Eliminando rol con ID: {}", id);
        return roleUseCase.deleteRole(id)
                .then(ServerResponse.noContent().build())
                .doOnSuccess(r -> log.info("Role eliminado con ID: {}", id))
                .doOnError(e -> log.error("Error eliminando rol: {}", e.getMessage()))
                .onErrorResume(this::handleError);
    }

    /**
     * Validar DTO de rol
     */
    private Role validateDto(RoleDTO dto) {
        var violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String errs = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new ValidationException(errs);
        }
        return Role.builder()
                .uniqueId(dto.getUniqueId())
                .name(dto.getNombre())
                .description(dto.getDescripcion())
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
