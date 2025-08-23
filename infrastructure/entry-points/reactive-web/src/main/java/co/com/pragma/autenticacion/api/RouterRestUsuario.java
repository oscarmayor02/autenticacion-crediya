package co.com.pragma.autenticacion.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRestUsuario {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(HandlerUsuario handler) {
        return route(POST("/api/v1/usuarios"), handler::registrarUsuario);
                                       // .and(route(GET("/api/v1/usuarios"), handler::UserGetAll));
//                                        .and(route(GET("/api/v1/usuarios/exists/{email}"), handler::UserExistsByEmail))
//                                        .and(route(GET("/api/v1/usuarios/exists/{id}"), handler::UserExistsById))
//                                        .and(route(POST("/api/v1/usuarios/login"), handler::UserLogin))
//                                        .and(route(POST("/api/v1/usuarios/logout"), handler::UserLogout))
//                                        .and(route(PATCH("/api/v1/usuarios/password/{id}"), handler::UserUpdatePassword))
//                                .and(route(GET("/api/v1/usuarios/{id}"), handler::UserGetById)

    }
}
