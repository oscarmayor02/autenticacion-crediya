package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.dto.RoleDTO;
import co.com.pragma.autenticacion.model.auth.AuthConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Router para exponer endpoints de ROLES.
 */
@Configuration
public class RouterRestRole {
    @Bean
    @RouterOperations({
            // GET ALL
            @RouterOperation(
                    path = AuthConstants.ROLE_BASE_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = HandlerRole.class,
                    beanMethod = "getAllRoles",
                    operation = @Operation(
                            operationId = "getAllRoles",
                            summary = "Obtener todos los roles",
                            responses = {@ApiResponse(responseCode = "200", description = "Lista de roles",
                                    content = @Content(schema = @Schema(implementation = RoleDTO.class)))}
                    )
            ),
            // GET BY ID
            @RouterOperation(
                    path = AuthConstants.ROLE_BASE_PATH + AuthConstants.ROLE_BY_ID_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = HandlerRole.class,
                    beanMethod = "obtenerRolPorId",
                    operation = @Operation(
                            operationId = "obtenerRolPorId",
                            summary = "Obtener un rol por ID",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Role encontrado",
                                            content = @Content(schema = @Schema(implementation = RoleDTO.class))),
                                    @ApiResponse(responseCode = "404", description = "Role no encontrado")
                            }
                    )
            ),
            // POST
            @RouterOperation(
                    path = AuthConstants.ROLE_BASE_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = HandlerRole.class,
                    beanMethod = "crearRol",
                    operation = @Operation(
                            operationId = "crearRol",
                            summary = "Registrar un nuevo rol",
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = RoleDTO.class))),
                            responses = {@ApiResponse(responseCode = "201", description = "Role creado")}
                    )
            ),
            // PUT
            @RouterOperation(
                    path = AuthConstants.ROLE_BASE_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.PUT,
                    beanClass = HandlerRole.class,
                    beanMethod = "actualizarRol",
                    operation = @Operation(
                            operationId = "actualizarRol",
                            summary = "Editar un rol existente",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Role actualizado"),
                                    @ApiResponse(responseCode = "404", description = "Role no encontrado")
                            }
                    )
            ),
            // DELETE
            @RouterOperation(
                    path = AuthConstants.ROLE_BASE_PATH + AuthConstants.ROLE_BY_ID_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.DELETE,
                    beanClass = HandlerRole.class,
                    beanMethod = "eliminarRol",
                    operation = @Operation(
                            operationId = "eliminarRol",
                            summary = "Eliminar un rol por ID",
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Role eliminado"),
                                    @ApiResponse(responseCode = "404", description = "Role no encontrado")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> rolRoutes(HandlerRole rolHandler) {
        return route(POST(AuthConstants.ROLE_BASE_PATH), rolHandler::crearRol)
                .andRoute(GET(AuthConstants.ROLE_BASE_PATH + AuthConstants.ROLE_BY_ID_PATH), rolHandler::getRoleById)
                .andRoute(PUT(AuthConstants.ROLE_BASE_PATH), rolHandler::updateRole)
                .andRoute(DELETE(AuthConstants.ROLE_BASE_PATH + AuthConstants.ROLE_BY_ID_PATH), rolHandler::deleteRole);
    }
}
