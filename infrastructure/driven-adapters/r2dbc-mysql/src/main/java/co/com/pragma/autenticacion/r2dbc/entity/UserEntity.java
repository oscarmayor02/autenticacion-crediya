package co.com.pragma.autenticacion.r2dbc.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
/**
 * Entidad que representa la tabla "usuarios".
 * Contiene toda la información básica del usuario.
 */
@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    @Column("id_usuario")
    private Long idUsuario;

    private String nombre;

    private String apellido;

    @Column("fecha_nacimiento")
    private String fechaNacimiento;

    private String direccion;

    private String password;

    private String telefono;

    @Column("correo_electronico")
    private String correoElectronico;

    @Column("salario_base")
    private BigDecimal salarioBase;

    @Column("documento_identidad")
    private String documentoIdentidad;

    @Column("rol_id")
    private BigDecimal rolId;
}