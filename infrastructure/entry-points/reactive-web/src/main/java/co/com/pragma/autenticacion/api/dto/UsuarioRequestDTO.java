package co.com.pragma.autenticacion.api.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {
    @NotBlank(message = "nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "apellido es obligatorio")
    private String apellido;

    @Email(message = "email inválido")
    @NotBlank(message = "email es obligatorio")
    private String email;

    @Pattern(regexp="^\\d{4}-\\d{2}-\\d{2}$", message="fecha_nacimiento debe ser yyyy-MM-dd")
    private String fecha_nacimiento;

    @Size(max = 30, message = "documento_identidad máximo 30 car.")
    private String documentoIdentidad;

    @Size(min = 7, max = 15, message = "El teléfono debe tener entre 7 y 15 dígitos")
    private String telefono;

    @NotNull(message = "salario_base es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "salario_base >= 0")
    @DecimalMax(value = "15000000.0", inclusive = true, message = "salario_base <= 15000000")
    private Double salarioBase;

    @NotBlank(message = "direccion es obligatoria")
    @Size(max = 200, message = "direccion máximo 200 car.")
    private String direccion;

    @NotNull(message = "idRol es obligatorio")
    @Min(value = 1, message = "idRol debe ser mayor a 0")
    private Integer rolId;

}
