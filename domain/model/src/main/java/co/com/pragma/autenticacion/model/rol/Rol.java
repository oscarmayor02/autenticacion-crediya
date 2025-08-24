package co.com.pragma.autenticacion.model.rol;
import lombok.*;
import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Rol {
    private Integer uniqueId;
    private String nombre;
    private String descripcion;
}
