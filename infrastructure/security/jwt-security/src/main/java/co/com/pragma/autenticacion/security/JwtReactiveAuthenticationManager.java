package co.com.pragma.autenticacion.security;


import co.com.pragma.autenticacion.model.auth.AuthConstants;
import co.com.pragma.autenticacion.model.tokeninfo.gateways.TokenProvider;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    // Puerto TokenProvider para parsear y validar tokens.
    private final TokenProvider tokenProvider;

    public JwtReactiveAuthenticationManager(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        // Extrae el token de las credenciales.
        String token = (String) authentication.getCredentials();
        // Llama a parseAndValidate para obtener los claims del token.
        return tokenProvider.parseAndValidate(token)
                .map(claims -> {
                    // Extrae la lista de roles desde los claims.
                    Object rolesObj = claims.get(AuthConstants.CLAIM_ROLES);
                    List<String> roles = rolesObj instanceof List ? (List<String>) rolesObj : List.of();

                    // Convierte cada rol a SimpleGrantedAuthority para Spring Security.
                    var authorities = roles.stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                            .collect(Collectors.toList());

                    // Obtiene el principal (email del usuario) del claim.
                    String rawPrincipal = (String) claims.get(AuthConstants.CLAIM_EMAIL);
                    if (rawPrincipal == null) {
                        rawPrincipal = (String) ((Map<String, Object>) claims).get(Claims.SUBJECT);
                    }
                    final String principal = rawPrincipal;

                    // Crea un Authentication personalizado con roles y principal.
                    Authentication auth = new AbstractAuthenticationToken(authorities) {
                        @Override public Object getCredentials() { return token; }
                        @Override public Object getPrincipal() { return principal; }
                    };
                    ((AbstractAuthenticationToken) auth).setAuthenticated(true);
                    return auth;
                })
                // Asegura el tipo correcto.
                .cast(Authentication.class)
                // Si hay error al validar el token, se lanza BadCredentialsException.
                .onErrorMap(e -> new BadCredentialsException("Token inválido", e));
    }
}