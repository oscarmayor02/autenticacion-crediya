package co.com.pragma.autenticacion.api.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TokenResponse {
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private long   expiresIn;
}