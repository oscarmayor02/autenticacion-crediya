package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.dto.UserRequestDTO;
import co.com.pragma.autenticacion.model.auth.AuthConstants;
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
@Tag(name = AuthConstants.MSG_NAME_SWAGGER, description = AuthConstants.MSG_USER_CRUD)
public class RouterRestUser {

    @Bean
    @RouterOperations({
            // GET ALL
            @RouterOperation(
                    path = AuthConstants.USERS_BASE_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = HandlerUsuario.class,
                    beanMethod = AuthConstants.MSG_GET,
                    operation = @Operation(
                            operationId = AuthConstants.MSG_GET,
                            summary = AuthConstants.MSG_GET_ALL_USERS,
                            responses = {
                                    @ApiResponse(responseCode = "200", description = AuthConstants.MSG_GET_USER_DESCRIPTION,
                                            content = @Content(schema = @Schema(implementation = UserRequestDTO.class)))
                            }
                    )
            ),
            // GET BY ID
            @RouterOperation(
                    path = AuthConstants.USERS_BASE_PATH + AuthConstants.PATH_ID,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = HandlerUsuario.class,
                    beanMethod = AuthConstants.MSG_GET_USER_BY_ID,
                    operation = @Operation(
                            operationId = AuthConstants.MSG_GET_USER_BY_ID,
                            summary = AuthConstants.MSG_GET_USER_BY_ID_DESCRIPTION,
                            responses = {
                                    @ApiResponse(responseCode = "200", description = AuthConstants.MSG_USER_FOUND_BY_ID,
                                            content = @Content(schema = @Schema(implementation = UserRequestDTO.class))),
                                    @ApiResponse(responseCode = "404", description = AuthConstants.MSG_USER_NOT_FOUND)
                            }
                    )
            ),
            // POST (crear usuario)
            @RouterOperation(
                    path = AuthConstants.USERS_BASE_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = HandlerUsuario.class,
                    beanMethod = AuthConstants.MSG_REGISTER_USER,
                    operation = @Operation(
                            operationId = AuthConstants.MSG_REGISTER_USER,
                            summary = AuthConstants.MSG_CREATE_USER,
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = UserRequestDTO.class))),
                            responses = {@ApiResponse(responseCode = "201", description = AuthConstants.MGS_USER_CREATE_OK)}
                    )
            ),
            // PUT (editar usuario)
            @RouterOperation(
                    path = AuthConstants.USERS_BASE_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.PUT,
                    beanClass = HandlerUsuario.class,
                    beanMethod = AuthConstants.MSG_UPDATE,
                    operation = @Operation(
                            operationId = AuthConstants.MSG_UPDATE,
                            summary = AuthConstants.MSG_USER_OK_UPDATE,
                            responses = {
                                    @ApiResponse(responseCode = "200", description = AuthConstants.MSG_UPDATE_USER),
                                    @ApiResponse(responseCode = "404", description = AuthConstants.MSG_USER_NOT_FOUND)
                            }
                    )
            ),
            // DELETE
            @RouterOperation(
                    path = AuthConstants.USERS_BASE_PATH + AuthConstants.PATH_ID,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.DELETE,
                    beanClass = HandlerUsuario.class,
                    beanMethod = AuthConstants.MSG_DELETE,
                    operation = @Operation(
                            operationId = AuthConstants.MSG_DELETE,
                            summary = AuthConstants.MSG_DELETE_USER,
                            responses = {
                                    @ApiResponse(responseCode = "204", description = AuthConstants.MSG_USER_DELETE_OK),
                                    @ApiResponse(responseCode = "404", description = AuthConstants.MSG_USER_NOT_FOUND)
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(HandlerUsuario handler) {
        // Asociación de rutas con métodos del handler
        return route(POST(AuthConstants.USERS_BASE_PATH), handler::registerUser)
                .andRoute(GET(AuthConstants.USERS_BASE_PATH), handler::getAllUsers)
                .andRoute(GET(AuthConstants.USERS_BASE_PATH + AuthConstants.PATH_ID), handler::getUserById)
                .andRoute(PUT(AuthConstants.USERS_BASE_PATH), handler::editUser)
                .andRoute(DELETE(AuthConstants.USERS_BASE_PATH + AuthConstants.PATH_ID), handler::deleteUser)
                .andRoute(GET(AuthConstants.USERS_BASE_PATH + AuthConstants.PATH_EXISTS_EMAIL), handler::existsByEmail)
                .andRoute(GET(AuthConstants.USERS_BASE_PATH + AuthConstants.PATH_EXISTS_DOC), handler::existsByDocument);
    }
}
