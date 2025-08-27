package co.com.pragma.autenticacion.model.tokeninfo.gateways;
import co.com.pragma.autenticacion.model.user.User;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface TokenProvider {
    Mono<String> generateAccessToken(User user, List<String> roles, long ttlMs);
    Mono<String> generateRefreshToken(User user, long ttlMs);
    Mono<Map<String, Object>> parseAndValidate(String token);
}
