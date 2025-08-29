package co.com.pragma.autenticacion.security;
import co.com.pragma.autenticacion.model.auth.AuthConstants;
import co.com.pragma.autenticacion.model.tokeninfo.gateways.TokenProvider;
import co.com.pragma.autenticacion.model.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtTokenProviderAdapter implements TokenProvider {
    // Llave secreta usada para firmar los tokens JWT.
    private final SecretKey secretKey;
    // Parser para validar y extraer claims de un JWT.
    private final JwtParser parser;

    // Constructor: inyecta la clave secreta desde application.properties con @Value.
    public JwtTokenProviderAdapter(@Value("${security.jwt.secret}") String secret) {
        // Genera la clave secreta a partir de la cadena de configuración.
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        // Construye un parser JWT usando la clave secreta para validación.
        this.parser = Jwts.parserBuilder().setSigningKey(this.secretKey).build();
    }

    // Genera un Access Token (token de corta duración con roles y datos del usuario).
    @Override
    public Mono<String> generateAccessToken(User user, List<String> roles, long ttlMs) {
        // Marca el tiempo actual.
        Date now = new Date();
        // Calcula la fecha de expiración sumando el TTL.
        Date exp = new Date(now.getTime() + ttlMs);
        // Construye el token con claims personalizados.
        String token = Jwts.builder()
                .setSubject(user.getEmail()) // Subject = email del usuario.
                .setIssuedAt(now) // Fecha de emisión.
                .setExpiration(exp) // Fecha de expiración.
                .claim(AuthConstants.CLAIM_ID, user.getIdNumber()) // Claim: ID del usuario.
                .claim(AuthConstants.CLAIM_EMAIL, user.getEmail()) // Claim: email.
                .claim(AuthConstants.CLAIM_NAME, user.getName() + " " + user.getLastName()) // Claim: nombre completo.
                .claim(AuthConstants.CLAIM_ROLES, roles) // Claim: lista de roles.
                .signWith(secretKey, SignatureAlgorithm.HS256) // Firma con HMAC SHA-256.
                .compact(); // Genera el token final.
        return Mono.just(token); // Retorna el token en un Mono.
    }

    // Genera un Refresh Token (solo contiene email, usado para renovar Access Tokens).
    @Override
    public Mono<String> generateRefreshToken(User user, long ttlMs) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttlMs);
        String token = Jwts.builder()
                .setSubject("refresh:" + user.getEmail()) // Subject indica que es un refresh.
                .setIssuedAt(now)
                .setExpiration(exp)
                .claim(AuthConstants.CLAIM_EMAIL, user.getEmail()) // Claim mínimo: email.
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        return Mono.just(token);
    }

    // Valida un token recibido y devuelve sus claims en un Map.
    @Override
    public Mono<Map<String, Object>> parseAndValidate(String token) {
        return Mono.fromCallable(() -> {
            // Parsea y valida el token usando el parser configurado.
            Jws<Claims> jws = parser.parseClaimsJws(token);
            // Retorna los claims como HashMap para manipulación sencilla.
            return new HashMap<>(jws.getBody());
        });
    }
}