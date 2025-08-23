package co.com.pragma.autenticacion.api.config;



import co.com.pragma.autenticacion.usecase.exceptions.DomainException;
import co.com.pragma.autenticacion.usecase.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@Order(-2) // antes que el DefaultErrorWebExceptionHandler
public class GlobalErrorHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Object body;

        if (ex instanceof ValidationException ve) {
            body = Map.of("code", "VALIDATION_ERROR", "errors", ve.getErrors());
        } else if (ex instanceof DomainException de) {
            body = Map.of("code", de.getCode(), "message", de.getMessage());
        } else {
            log.error("Error no controlado", ex);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            body = Map.of("code", "INTERNAL_ERROR", "message", "Ha ocurrido un error");
        }

        var json = "{\"" + body.toString().replace("=", "\":\"")
                .replace(", ", "\",\"").replace("{", "").replace("}", "") + "\"}";

        var resp = exchange.getResponse();
        resp.setStatusCode(status);
        resp.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return resp.writeWith(Mono.just(resp.bufferFactory()
                .wrap(json.getBytes(StandardCharsets.UTF_8))));
    }
}