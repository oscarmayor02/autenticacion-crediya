package co.com.pragma.autenticacion.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Autenticación - CrediYa")
                        .version("1.0.0")
                        .description("Documentación completa para autenticación y gestión de usuarios/roles para bootcamp CrediYa PRAGMA.")
                        .termsOfService("https://www.crediya.com/terms")
                        .contact(new Contact().name("OSCAR EDUARDO MAYOR JARAMILLO - oscar.amyor.dev@gmail.com").email("oscar.amyor.dev@gmail.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}