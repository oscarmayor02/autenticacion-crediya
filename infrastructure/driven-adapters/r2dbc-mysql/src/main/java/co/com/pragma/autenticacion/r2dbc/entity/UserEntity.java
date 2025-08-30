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
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    @Column("id_user")
    private Long idUser;

    private String name;
    @Column("last_name")
    private String lastName;

    @Column("date_of_birth")
    private String dateOfBirth;

    private String address;

    private String password;

    private String telephone;

    @Column("email")
    private String email;

    @Column("base_salary")
    private BigDecimal baseSalary;

    @Column("identity_document")
    private String identityDocument;

    @Column("role_id")
    private BigDecimal roleId;
}