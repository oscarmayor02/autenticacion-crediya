package co.com.pragma.autenticacion.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Mapper de MapStruct para convertir entre:
 * - Entidad persistente (RoleEntity)
 * - Modelo de dominio (Role)
 */
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RoleEntity {
    @Id
    @Column("role_id")
    private Long idRole;
    private String name;
    private String description;
}