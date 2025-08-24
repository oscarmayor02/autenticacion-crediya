package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.dto.RolDTO;
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

@Configuration
public class RouterRestRole {
    @Bean
    @RouterOperations({

            @RouterOperation(
                    path = "/api/v1/roles",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = HandlerRole.class,
                    beanMethod = "getAllRoles", // ⚠️ verifica que sí exista en HandlerRole
                    operation = @Operation(
                            operationId = "getAllRoles",
                            summary = "Obtener todos los roles",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Lista de roles",
                                            content = @Content(schema = @Schema(implementation = RolDTO.class))
                                    )
                            }
                    )
            ),

            @RouterOperation(
                    path = "/api/v1/roles/{uniqueId}",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = HandlerRole.class,
                    beanMethod = "obtenerRolPorId", // 🔄 cambiado
                    operation = @Operation(
                            operationId = "obtenerRolPorId",
                            summary = "Obtener un rol por ID",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Rol encontrado",
                                            content = @Content(schema = @Schema(implementation = RolDTO.class))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
                            }
                    )
            ),

            @RouterOperation(
                    path = "/api/v1/roles",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = HandlerRole.class,
                    beanMethod = "crearRol", // 🔄 cambiado
                    operation = @Operation(
                            operationId = "crearRol",
                            summary = "Registrar un nuevo rol",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = RolDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Rol creado")
                            }
                    )
            ),

            @RouterOperation(
                    path = "/api/v1/roles",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.PUT,
                    beanClass = HandlerRole.class,
                    beanMethod = "actualizarRol", // 🔄 cambiado
                    operation = @Operation(
                            operationId = "actualizarRol",
                            summary = "Editar un rol existente",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = RolDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Rol actualizado"),
                                    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
                            }
                    )
            ),

            @RouterOperation(
                    path = "/api/v1/roles/{uniqueId}",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.DELETE,
                    beanClass = HandlerRole.class,
                    beanMethod = "eliminarRol", // 🔄 cambiado
                    operation = @Operation(
                            operationId = "eliminarRol",
                            summary = "Eliminar un rol por ID",
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Rol eliminado"),
                                    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> rolRoutes(HandlerRole rolHandler) {
        return route(POST("/api/v1/roles"), rolHandler::crearRol)
                .andRoute(GET("/api/v1/roles/{uniqueId}"), rolHandler::obtenerRolPorId)
                .andRoute(PUT("/api/v1/roles"), rolHandler::actualizarRol)
                .andRoute(DELETE("/api/v1/roles/{uniqueId}"), rolHandler::eliminarRol);
    }
}
