package co.com.pragma.autenticacion.api.dto;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginRequest {
    private String email;
    private String password;
}