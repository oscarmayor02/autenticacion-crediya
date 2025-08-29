package co.com.pragma.autenticacion.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto que representa la respuesta al consultar un usuario")
public class UserResponseDTO {

    @Schema(description = "Nombre del usuario", example = "Juan")
    private String name;

    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String lastName;

    @Schema(description = "Fecha de nacimiento del usuario", example = "1990-05-21")
    private String dateOfBirth;

    @Schema(description = "Documento de identidad", example = "1234567890")
    private String identityDocument;

    @Schema(description = "Teléfono del usuario", example = "3001234567")
    private String telephone;

    @Schema(description = "Salario base", example = "15000000")
    private Double baseSalary;

    @Schema(description = "Dirección de residencia", example = "Calle 123 #45-67")
    private String address;

    @Schema(description = "Rol del usuario")
    private RolResponseDTO role;

    // DTO anidado para el rol
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RolResponseDTO {
        private Integer id;
        private String name;
    }
}
