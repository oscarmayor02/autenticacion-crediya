package co.com.pragma.autenticacion.api.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshRequest {
    private String refreshToken;
}