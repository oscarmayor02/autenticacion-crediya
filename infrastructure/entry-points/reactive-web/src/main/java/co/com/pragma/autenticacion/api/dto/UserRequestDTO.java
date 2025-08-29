package co.com.pragma.autenticacion.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto que representa la petición para crear o actualizar un usuario")
public class UserRequestDTO {

    @NotBlank(message = "nombre es obligatorio")
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String name;

    @NotBlank(message = "apellido es obligatorio")
    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String lastName;

    @Email(message = "email inválido")
    @NotBlank(message = "email es obligatorio")
    @Schema(description = "Correo electrónico del usuario", example = "juan.perez@empresa.com")
    private String email;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "fecha_nacimiento debe ser yyyy-MM-dd")
    @Schema(description = "Fecha de nacimiento del usuario (yyyy-MM-dd)", example = "1990-05-21")
    private String dateOfBirth;

    @Size(max = 30, message = "documento_identidad máximo 30 car.")
    @Schema(description = "Documento de identidad del usuario", example = "1234567890")
    private String identityDocument;

    @Size(min = 7, max = 15, message = "El teléfono debe tener entre 7 y 15 dígitos")
    @Schema(description = "Número telefónico del usuario", example = "3001234567")
    private String telephone;

    @NotNull(message = "salario_base es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "salario debe de ser mayor a 0")
    @DecimalMax(value = "15000000.0", inclusive = true, message = "el salario no debe ser mayor a 15000000")
    @Schema(description = "Salario base del usuario", example = "2500000")
    private Double baseSalary;

    @NotBlank(message = "direccion es obligatoria")
    @Size(max = 200, message = "direccion máximo 200 car.")
    @Schema(description = "Dirección de residencia", example = "Calle 123 #45-67")
    private String address;

    @NotNull(message = "idRol es obligatorio")
    @Min(value = 1, message = "idRol debe ser mayor a 0")
    @Schema(description = "ID del rol asignado al usuario", example = "1")
    private Integer roleId;

    // NUEVO: password plano para creación (no exponer en respuestas)
    @NotBlank(message = "password es obligatorio")
    @Size(min = 6, max = 100, message = "password debe tener entre 6 y 100 caracteres")
    private String password;
}
