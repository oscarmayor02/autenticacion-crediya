package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.dto.UsuarioRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * Router funcional para exponer endpoints relacionados con USUARIOS.
 * Usa programación funcional de Spring WebFlux.
 */
@Configuration
@Tag(name = "Usuarios API", description = "Operaciones CRUD para Usuarios")
public class RouterRestUsuario {

    // ================== Constantes de Rutas ==================
    private static final String BASE_PATH = "/api/v1/usuarios";
    private static final String PATH_ID = "/{id}";
    private static final String PATH_EXISTS_EMAIL = "/exists/email/{email}";
    private static final String PATH_EXISTS_DOC = "/exists/documento/{documento}";

    @Bean
    @RouterOperations({
            // GET ALL
            @RouterOperation(
                    path = BASE_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = HandlerUsuario.class,
                    beanMethod = "getAllUsers",
                    operation = @Operation(
                            operationId = "getAllUsers",
                            summary = "Obtener todos los usuarios",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Lista de usuarios",
                                            content = @Content(schema = @Schema(implementation = UsuarioRequestDTO.class)))
                            }
                    )
            ),
            // GET BY ID
            @RouterOperation(
                    path = BASE_PATH + PATH_ID,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = HandlerUsuario.class,
                    beanMethod = "getUserById",
                    operation = @Operation(
                            operationId = "getUserById",
                            summary = "Obtener un usuario por ID",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                                            content = @Content(schema = @Schema(implementation = UsuarioRequestDTO.class))),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
                            }
                    )
            ),
            // POST (crear usuario)
            @RouterOperation(
                    path = BASE_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = HandlerUsuario.class,
                    beanMethod = "registrarUsuario",
                    operation = @Operation(
                            operationId = "registrarUsuario",
                            summary = "Registrar un nuevo usuario",
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = UsuarioRequestDTO.class))),
                            responses = {@ApiResponse(responseCode = "201", description = "Usuario creado")}
                    )
            ),
            // PUT (editar usuario)
            @RouterOperation(
                    path = BASE_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.PUT,
                    beanClass = HandlerUsuario.class,
                    beanMethod = "editUser",
                    operation = @Operation(
                            operationId = "editUser",
                            summary = "Editar un usuario existente",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
                            }
                    )
            ),
            // DELETE
            @RouterOperation(
                    path = BASE_PATH + PATH_ID,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.DELETE,
                    beanClass = HandlerUsuario.class,
                    beanMethod = "deleteUser",
                    operation = @Operation(
                            operationId = "deleteUser",
                            summary = "Eliminar un usuario por ID",
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(HandlerUsuario handler) {
        // Asociación de rutas con métodos del handler
        return route(POST(BASE_PATH), handler::registrarUsuario)
                .andRoute(GET(BASE_PATH), handler::getAllUsers)
                .andRoute(GET(BASE_PATH + PATH_ID), handler::getUserById)
                .andRoute(PUT(BASE_PATH), handler::editUser)
                .andRoute(DELETE(BASE_PATH + PATH_ID), handler::deleteUser)
                .andRoute(GET(BASE_PATH + PATH_EXISTS_EMAIL), handler::existsByEmail)
                .andRoute(GET(BASE_PATH + PATH_EXISTS_DOC), handler::existsByDocumento);
    }
}
