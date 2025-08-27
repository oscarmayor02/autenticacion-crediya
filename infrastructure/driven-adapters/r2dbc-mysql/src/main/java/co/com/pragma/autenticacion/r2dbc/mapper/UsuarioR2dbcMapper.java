package co.com.pragma.autenticacion.r2dbc.mapper;

import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.r2dbc.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

/**
 * Mapper de MapStruct para convertir entre:
 * - UserEntity (persistencia, tabla usuarios)
 * - User (modelo de dominio)
 */
@Mapper(componentModel = "spring", implementationName = "UsuarioR2dbcMapperImpl")
public interface UsuarioR2dbcMapper {

    // Convierte UserEntity -> User (dominio)
    @Mapping(target = "idNumber", source = "idUsuario")
    @Mapping(target = "idRol", source = "rolId")
    User toModel(UserEntity entity);

    // Convierte User -> UserEntity (persistencia)
    @Mapping(target = "idUsuario", source = "idNumber")
    @Mapping(target = "rolId", source = "idRol")
    UserEntity toEntity(User user);

    // Conversión opcional Long <-> BigDecimal
    default BigDecimal map(Long value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }

    default Long map(BigDecimal value) {
        return value != null ? value.longValue() : null;
    }
}
