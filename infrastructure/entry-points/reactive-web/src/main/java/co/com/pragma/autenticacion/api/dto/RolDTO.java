package co.com.pragma.autenticacion.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RolDTO {

    private Integer uniqueId;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String nombre;

    @Size(max = 200, message = "La descripción no debe superar 200 caracteres")
    private String descripcion;
}