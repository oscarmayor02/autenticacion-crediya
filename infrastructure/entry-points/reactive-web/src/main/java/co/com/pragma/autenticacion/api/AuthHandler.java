package co.com.pragma.autenticacion.api;


import co.com.pragma.autenticacion.api.dto.LoginRequest;
import co.com.pragma.autenticacion.api.dto.RefreshRequest;
import co.com.pragma.autenticacion.api.dto.TokenResponse;
import co.com.pragma.autenticacion.model.auth.AuthCredentials;
import co.com.pragma.autenticacion.model.tokeninfo.TokenInfo;
import co.com.pragma.autenticacion.usecase.auth.AuthUseCase;
import co.com.pragma.autenticacion.usecase.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final AuthUseCase authUseCase;

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginRequest.class)
                .switchIfEmpty(Mono.error(new ValidationException("Body requerido")))
                .flatMap(body -> authUseCase.login(AuthCredentials.builder()
                        .email(body.getEmail())
                        .password(body.getPassword())
                        .build()))
                .map(this::toResponse)
                .flatMap(resp -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(resp))
                .doOnSuccess(r -> log.info("Login exitoso"))
                .onErrorResume(ErrorResponses::toResponse);
    }

    public Mono<ServerResponse> refresh(ServerRequest request) {
        return request.bodyToMono(RefreshRequest.class)
                .switchIfEmpty(Mono.error(new ValidationException("Body requerido")))
                .flatMap(body -> authUseCase.refresh(body.getRefreshToken()))
                .map(this::toResponse)
                .flatMap(resp -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(resp))
                .doOnSuccess(r -> log.info("Refresh exitoso"))
                .onErrorResume(ErrorResponses::toResponse);
    }

    private TokenResponse toResponse(TokenInfo t) {
        return TokenResponse.builder()
                .tokenType(t.getTokenType())
                .accessToken(t.getAccessToken())
                .refreshToken(t.getRefreshToken())
                .expiresIn(t.getExpiresIn())
                .build();
    }
}