package co.com.pragma.autenticacion.usecase.auth;

import co.com.pragma.autenticacion.model.auth.AuthConstants;
import co.com.pragma.autenticacion.model.auth.AuthCredentials;
import co.com.pragma.autenticacion.model.auth.gateways.PasswordEncoderPort;
import co.com.pragma.autenticacion.model.tokeninfo.TokenInfo;
import co.com.pragma.autenticacion.model.tokeninfo.gateways.TokenProvider;
import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.model.user.gateways.UserRepository;
import co.com.pragma.autenticacion.usecase.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class AuthUseCase {

    // Puerto para acceder a usuarios (permite mockear en tests y cambiar implementación sin tocar el caso de uso).
    private final UserRepository userRepository;
    // Puerto para generar y validar tokens (se inyecta implementación concreta, p.ej., JWT).
    private final TokenProvider tokenProvider;
    // Puerto para comparar contraseñas (abstrae BCrypt/Argon2, etc.).
    private final PasswordEncoderPort passwordEncoderPort;
    // Estrategia para resolver los roles de un usuario (permite centralizar lógica de roles/permisos).
    private final RolesResolver rolesResolver;

    // Caso de uso: autenticar con credenciales y devolver access/refresh tokens.
    public Mono<TokenInfo> login(AuthCredentials credentials) {
        // Validación defensiva: si faltan email o password, retorna error reactivo inmediatamente.
        if (credentials == null || isBlank(credentials.getEmail()) || isBlank(credentials.getPassword())) {
            return Mono.error(new ValidationException(AuthConstants.MGS_FIELD_REQUIRED));
        }

        // Busca el usuario por email de forma reactiva.
        return userRepository.getByEmail(credentials.getEmail())
                // Si no existe el usuario, emite error de credenciales inválidas (no revela si falló email o password).
                .switchIfEmpty(Mono.error(new ValidationException(AuthConstants.MSG_INVALID_CREDENTIALS)))

                // Si existe, continúa con la verificación de la contraseña.
                .flatMap(user -> passwordEncoderPort.matches(credentials.getPassword(), user.getPassword())
                        // 'matches' devuelve Mono<Boolean>; aquí evaluamos el resultado.
                        .flatMap(matches -> {

                            // Si la contraseña no coincide, devolvemos el mismo error genérico.
                            if (!matches)
                                return Mono.error(new ValidationException(AuthConstants.MSG_INVALID_CREDENTIALS));

                            // Resuelve los roles efectivos del usuario (lista de strings) según la estrategia inyectada.
                            List<String> roles = rolesResolver.resolve(user);

                            // En paralelo (zip) genera access y refresh token.
                            return Mono.zip(
                                            // Genera access token con TTL (milisegundos) y claims de roles.
                                            tokenProvider.generateAccessToken(user, roles, AuthConstants.ACCESS_TOKEN_TTL_MS),
                                            // Genera refresh token con su propio TTL (usualmente más largo).
                                            tokenProvider.generateRefreshToken(user, AuthConstants.REFRESH_TOKEN_TTL_MS)
                                    )
                                    // Con ambos tokens, construye el DTO de respuesta TokenInfo.
                                    .map(t -> TokenInfo.builder()
                                            // Estándar de esquema para Authorization header (p.ej., "Bearer <token>").
                                            .tokenType("Bearer")
                                            // Primer elemento del zip (T1) es el access token.
                                            .accessToken(t.getT1())
                                            // Segundo elemento (T2) es el refresh token.
                                            .refreshToken(t.getT2())
                                            // Expiración del access token en segundos (API-friendly).
                                            .expiresIn(AuthConstants.ACCESS_TOKEN_TTL_MS / 1000)
                                            .build());
                        })
                );
    }

    // Caso de uso: renovar tokens a partir de un refresh token válido.
    public Mono<TokenInfo> refresh(String refreshToken) {
        // Validación: si el refresh token viene vacío/nulo, error inmediato.
        if (isBlank(refreshToken))
            return Mono.error(new ValidationException(AuthConstants.MSG_INVALID_TOKEN));

        // Parsea y valida el refresh token (firma, expiración, estructura, etc.).
        return tokenProvider.parseAndValidate(refreshToken)
                // A partir de los claims, obtén el email y con él recarga al usuario desde el repositorio.
                .flatMap(claims -> {
                    // Extrae el claim de email según la clave estándar definida en AuthConstants.
                    Object email = claims.get(AuthConstants.CLAIM_EMAIL);
                    // Si el claim no está, el token es inválido para nuestra lógica.
                    if (email == null)
                        return Mono.error(new ValidationException(AuthConstants.MSG_INVALID_TOKEN));
                    // Recupera el usuario para emitir nuevos tokens con su información/roles actualizados.
                    return userRepository.getByEmail(email.toString());
                })
                // Con el usuario encontrado, vuelve a calcular roles y emite nuevos tokens (rotate tokens).
                .flatMap(user -> {
                    // Resolución de roles al momento del refresh (por si cambiaron en el tiempo).
                    List<String> roles = rolesResolver.resolve(user);
                    // Genera nuevamente access y refresh para mantener la sesión activa.
                    return Mono.zip(
                                    tokenProvider.generateAccessToken(user, roles, AuthConstants.ACCESS_TOKEN_TTL_MS),
                                    tokenProvider.generateRefreshToken(user, AuthConstants.REFRESH_TOKEN_TTL_MS)
                            )
                            // Construye el TokenInfo de salida igual que en login.
                            .map(t -> TokenInfo.builder()
                                    .tokenType("Bearer")
                                    .accessToken(t.getT1())
                                    .refreshToken(t.getT2())
                                    .expiresIn(AuthConstants.ACCESS_TOKEN_TTL_MS / 1000)
                                    .build());
                });
    }

    // Utilidad local para validar strings en blanco (evita duplicar lógica de null/trim).
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // Contrato para una estrategia de resolución de roles: facilita testear y cambiar la fuente de roles.
    public interface RolesResolver {
        // Dado un usuario, retorna los roles que aplican (p.ej., ["ROLE_ADMIN", "ROLE_USER"]).
        List<String> resolve(User user);
    }
}