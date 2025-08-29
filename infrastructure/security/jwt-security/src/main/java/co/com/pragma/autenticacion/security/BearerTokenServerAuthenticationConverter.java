package co.com.pragma.autenticacion.security;
import co.com.pragma.autenticacion.model.auth.AuthConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class BearerTokenServerAuthenticationConverter implements ServerAuthenticationConverter {
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        // Obtiene el header Authorization de la petición.
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        // Si existe y empieza por "Bearer ", extrae el token.
        if (header != null && header.startsWith(AuthConstants.BEARER_PREFIX)) {
            String token = header.substring(AuthConstants.BEARER_PREFIX.length());
            // Devuelve un UsernamePasswordAuthenticationToken con el token como credenciales y principal.
            return Mono.just(new UsernamePasswordAuthenticationToken(token, token));
        }
        // Si no hay token, retorna Mono vacío (no autenticado).
        return Mono.empty();
    }
}