package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.usecase.exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class ErrorResponses {

    public static Mono<ServerResponse> toResponse(Throwable error) {
        if (error instanceof ValidationException) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .bodyValue(error.getMessage());
        }
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue("Error interno: " + error.getMessage());
    }
}
