package co.com.pragma.autenticacion.security.config;

import co.com.pragma.autenticacion.model.auth.AuthConstants;
import co.com.pragma.autenticacion.security.JwtAuthenticationWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            JwtAuthenticationWebFilter jwtFilter) {
        return http
                // Deshabilita CSRF (útil para APIs).
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // Deshabilita autenticación básica.
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                // Deshabilita formulario de login.
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                // Configura reglas de autorización.
                .authorizeExchange(ex -> ex
                        // Endpoints públicos.
                        .pathMatchers(AuthConstants.LOGIN_PATH, AuthConstants.REFRESH_PATH,
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**").permitAll()
                        // Registro de usuarios permitido a cualquiera.
                        .pathMatchers(HttpMethod.POST, "/api/v1/usuarios").permitAll()
                        // Endpoints restringidos a roles específicos.
                        .pathMatchers("/api/v1/usuarios/**")
                        .hasAnyRole(AuthConstants.ADMIN_ROLE, AuthConstants.ADVISOR_ROLE)
                        .pathMatchers(HttpMethod.GET, "/api/v1/solicitud")
                        .hasRole(AuthConstants.ADVISOR_ROLE)
                        .pathMatchers(HttpMethod.POST, "/api/v1/solicitud")
                        .hasRole(AuthConstants.CLIENT_ROLE)
                        // Cualquier otro endpoint requiere estar autenticado.
                        .anyExchange().authenticated()
                )
                // Agrega el filtro JWT en la posición correcta de la cadena.
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                // Construye y retorna el filtro final.
                .build();
    }
}