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

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoderPort passwordEncoderPort;
    private final RolesResolver rolesResolver;

    public Mono<TokenInfo> login(AuthCredentials credentials) {
        System.out.println(credentials);
        if (credentials == null || isBlank(credentials.getEmail()) || isBlank(credentials.getPassword())) {
            return Mono.error(new ValidationException("email y password son obligatorios"));
        }

        return userRepository.getByEmail(credentials.getEmail())
                .switchIfEmpty(Mono.error(new ValidationException(AuthConstants.MSG_INVALID_CREDENTIALS)))

                .flatMap(user -> passwordEncoderPort.matches(credentials.getPassword(), user.getPassword())
                        .flatMap(matches -> {

                            if (!matches)
                                return Mono.error(new ValidationException(AuthConstants.MSG_INVALID_CREDENTIALS));
                            List<String> roles = rolesResolver.resolve(user);
                            return Mono.zip(
                                    tokenProvider.generateAccessToken(user, roles, AuthConstants.ACCESS_TOKEN_TTL_MS),
                                    tokenProvider.generateRefreshToken(user, AuthConstants.REFRESH_TOKEN_TTL_MS)
                            ).map(t -> TokenInfo.builder()
                                    .tokenType("Bearer")
                                    .accessToken(t.getT1())
                                    .refreshToken(t.getT2())
                                    .expiresIn(AuthConstants.ACCESS_TOKEN_TTL_MS / 1000)
                                    .build());
                        })
                );
    }

    public Mono<TokenInfo> refresh(String refreshToken) {
        if (isBlank(refreshToken))
            return Mono.error(new ValidationException(AuthConstants.MSG_INVALID_TOKEN));

        return tokenProvider.parseAndValidate(refreshToken)
                .flatMap(claims -> {
                    Object email = claims.get(AuthConstants.CLAIM_EMAIL);
                    if (email == null)
                        return Mono.error(new ValidationException(AuthConstants.MSG_INVALID_TOKEN));
                    return userRepository.getByEmail(email.toString());
                })
                .flatMap(user -> {
                    List<String> roles = rolesResolver.resolve(user);
                    return Mono.zip(
                            tokenProvider.generateAccessToken(user, roles, AuthConstants.ACCESS_TOKEN_TTL_MS),
                            tokenProvider.generateRefreshToken(user, AuthConstants.REFRESH_TOKEN_TTL_MS)
                    ).map(t -> TokenInfo.builder()
                            .tokenType("Bearer")
                            .accessToken(t.getT1())
                            .refreshToken(t.getT2())
                            .expiresIn(AuthConstants.ACCESS_TOKEN_TTL_MS / 1000)
                            .build());
                });
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public interface RolesResolver {
        List<String> resolve(User user);
    }
}
