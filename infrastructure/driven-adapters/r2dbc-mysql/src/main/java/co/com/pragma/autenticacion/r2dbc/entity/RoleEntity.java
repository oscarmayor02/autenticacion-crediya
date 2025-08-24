package co.com.pragma.autenticacion.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RoleEntity {
    @Id
    @Column("rol_id")
    private Long idRol;
    private String nombre;
    private String descripcion;
}