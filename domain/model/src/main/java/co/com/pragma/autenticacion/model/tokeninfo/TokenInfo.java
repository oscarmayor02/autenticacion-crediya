package co.com.pragma.autenticacion.model.tokeninfo;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TokenInfo {
    private String tokenType;      // "Bearer"
    private String accessToken;
    private String refreshToken;
    private long   expiresIn;      // segundos (del access token)
}
