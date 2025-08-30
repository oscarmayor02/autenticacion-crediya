package co.com.pragma.autenticacion.security.config;

import co.com.pragma.autenticacion.model.auth.gateways.PasswordEncoderPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;

@Configuration
public class EncoderConfig {

    // Declara un bean de BCryptPasswordEncoder para que Spring lo gestione como dependencia reutilizable.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Declara un bean que implementa PasswordEncoderPort usando BCryptPasswordEncoder.
    @Bean
    public PasswordEncoderPort passwordEncoderPort(BCryptPasswordEncoder encoder) {
        // Implementa PasswordEncoderPort como clase anónima.
        return new PasswordEncoderPort() {

            // Método para encriptar una contraseña de texto plano.
            @Override
            public Mono<String> encode(String raw) {
                // Devuelve un Mono que ejecuta la encriptación de forma asíncrona (no bloqueante).
                return Mono.fromCallable(() -> encoder.encode(raw));
            }

            // Método para verificar si una contraseña coincide con su hash.
            @Override
            public Mono<Boolean> matches(String raw, String encoded) {
                // Retorna un Mono con el resultado de la verificación de BCrypt.
                return Mono.fromCallable(() -> encoder.matches(raw, encoded));
            }
        };
    }
}