package co.com.pragma.autenticacion.model.auth.gateways;

import reactor.core.publisher.Mono;

public interface PasswordEncoderPort {
    Mono<String> encode(String raw);
    Mono<Boolean> matches(String raw, String encoded);
}
