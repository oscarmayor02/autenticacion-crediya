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

/**
 * Router para exponer endpoints de ROLES.
 */
@Configuration
public class RouterRestRole {

    // Constantes
    private static final String BASE_PATH = "/api/v1/roles";
    private static final String PATH_ID = "/{uniqueId}";

    @Bean
    @RouterOperations({
            // GET ALL
            @RouterOperation(
                    path = BASE_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = HandlerRole.class,
                    beanMethod = "getAllRoles",
                    operation = @Operation(
                            operationId = "getAllRoles",
                            summary = "Obtener todos los roles",
                            responses = {@ApiResponse(responseCode = "200", description = "Lista de roles",
                                    content = @Content(schema = @Schema(implementation = RolDTO.class)))}
                    )
            ),
            // GET BY ID
            @RouterOperation(
                    path = BASE_PATH + PATH_ID,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = HandlerRole.class,
                    beanMethod = "obtenerRolPorId",
                    operation = @Operation(
                            operationId = "obtenerRolPorId",
                            summary = "Obtener un rol por ID",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Rol encontrado",
                                            content = @Content(schema = @Schema(implementation = RolDTO.class))),
                                    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
                            }
                    )
            ),
            // POST
            @RouterOperation(
                    path = BASE_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = HandlerRole.class,
                    beanMethod = "crearRol",
                    operation = @Operation(
                            operationId = "crearRol",
                            summary = "Registrar un nuevo rol",
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = RolDTO.class))),
                            responses = {@ApiResponse(responseCode = "201", description = "Rol creado")}
                    )
            ),
            // PUT
            @RouterOperation(
                    path = BASE_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.PUT,
                    beanClass = HandlerRole.class,
                    beanMethod = "actualizarRol",
                    operation = @Operation(
                            operationId = "actualizarRol",
                            summary = "Editar un rol existente",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Rol actualizado"),
                                    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
                            }
                    )
            ),
            // DELETE
            @RouterOperation(
                    path = BASE_PATH + PATH_ID,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.DELETE,
                    beanClass = HandlerRole.class,
                    beanMethod = "eliminarRol",
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
        return route(POST(BASE_PATH), rolHandler::crearRol)
                .andRoute(GET(BASE_PATH + PATH_ID), rolHandler::obtenerRolPorId)
                .andRoute(PUT(BASE_PATH), rolHandler::actualizarRol)
                .andRoute(DELETE(BASE_PATH + PATH_ID), rolHandler::eliminarRol);
    }
}
