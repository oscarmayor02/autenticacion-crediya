package co.com.pragma.autenticacion.api;

import co.com.pragma.autenticacion.api.dto.LoginRequest;
import co.com.pragma.autenticacion.api.dto.RefreshRequest;
import co.com.pragma.autenticacion.api.dto.TokenResponse;
import co.com.pragma.autenticacion.model.auth.AuthConstants;
import co.com.pragma.autenticacion.model.auth.AuthCredentials;
import co.com.pragma.autenticacion.model.tokeninfo.TokenInfo;
import co.com.pragma.autenticacion.usecase.auth.AuthUseCase;
import co.com.pragma.autenticacion.usecase.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthHandler {

    // Caso de uso de autenticación, inyectado automáticamente
    private final AuthUseCase authUseCase;

    /**
     * Login de usuario.
     * Recibe un LoginRequest, valida que no esté vacío y llama al caso de uso.
     * Luego convierte el resultado a TokenResponse y retorna un ServerResponse.
     */
    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginRequest.class) // Lee el cuerpo de la petición como LoginRequest
                .switchIfEmpty(Mono.error(new ValidationException(AuthConstants.MSG_BODY_REQUIRED))) // Valida que exista body
                .flatMap(body -> authUseCase.login(
                        AuthCredentials.builder()
                                .email(body.getEmail())
                                .password(body.getPassword())
                                .build()
                )) // Llama al caso de uso, devuelve Mono<TokenInfo>
                .map(this::toResponse) // Convierte TokenInfo a TokenResponse (DTO para API)
                .flatMap(resp -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(resp)) // Construye el ServerResponse con JSON
                .doOnSuccess(r -> log.info(AuthConstants.VALIDATION_LOGIN_SUCCESS)) // Log si fue exitoso
                .onErrorResume(ErrorResponses::toResponse); // Manejo centralizado de errores
    }

    /**
     * Refresh de token.
     * Recibe un RefreshRequest, valida y llama al caso de uso de refresh.
     * Retorna el TokenResponse actualizado.
     */
    public Mono<ServerResponse> refresh(ServerRequest request) {
        return request.bodyToMono(RefreshRequest.class) // Lee body como RefreshRequest
                .switchIfEmpty(Mono.error(new ValidationException(AuthConstants.MSG_BODY_REQUIRED))) // Valida que exista body
                .flatMap(body -> authUseCase.refresh(body.getRefreshToken())) // Llama al caso de uso refresh
                .map(this::toResponse) // Convierte TokenInfo a TokenResponse
                .flatMap(resp -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(resp)) // Construye ServerResponse con JSON
                .doOnSuccess(r -> log.info(AuthConstants.VALIDATION_REFRESH_SUCCESS)) // Log éxito
                .onErrorResume(ErrorResponses::toResponse); // Manejo de errores centralizado
    }

    /**
     * Convierte TokenInfo del caso de uso a TokenResponse DTO de API.
     * @param t TokenInfo
     * @return TokenResponse
     */
    private TokenResponse toResponse(TokenInfo t) {
        return TokenResponse.builder()
                .tokenType(t.getTokenType()) // Tipo de token (ej. Bearer)
                .accessToken(t.getAccessToken()) // Token de acceso
                .refreshToken(t.getRefreshToken()) // Token de refresh
                .expiresIn(t.getExpiresIn()) // Tiempo de expiración
                .build();
    }
}
