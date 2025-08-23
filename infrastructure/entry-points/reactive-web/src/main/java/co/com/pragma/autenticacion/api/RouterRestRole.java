package co.com.pragma.autenticacion.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRestRole {
    @Bean
    public RouterFunction<ServerResponse> rolRoutes(HandlerRole rolHandler) {
        return route(POST("/api/v1/roles"), rolHandler::crearRol)
                .andRoute(GET("/api/v1/roles/{uniqueId}"), rolHandler::obtenerRolPorId);
    }
}
