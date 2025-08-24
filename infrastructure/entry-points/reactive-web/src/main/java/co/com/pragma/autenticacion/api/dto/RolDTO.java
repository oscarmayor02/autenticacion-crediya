package co.com.pragma.autenticacion.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Objeto que representa un rol dentro del sistema")
public class RolDTO {

    @Schema(description = "Identificador único del rol", example = "1")
    private Integer uniqueId;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nombre del rol", example = "ADMIN")
    private String nombre;

    @Size(max = 200, message = "La descripción no debe superar 200 caracteres")
    @Schema(description = "Descripción del rol", example = "Rol con permisos de administración total")
    private String descripcion;
}
