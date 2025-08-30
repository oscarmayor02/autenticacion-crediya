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
@Mapper(componentModel = "spring", implementationName = "UserMapperImpl")
public interface UserMapper {

    // Convierte UserEntity -> User (dominio)
    @Mapping(target = "idNumber", source = "idUser")
    @Mapping(target = "idRole", source = "roleId")
    User toModel(UserEntity entity);

    // Convierte User -> UserEntity (persistencia)
    @Mapping(target = "idUser", source = "idNumber")
    @Mapping(target = "roleId", source = "idRole")
    UserEntity toEntity(User user);

    // Conversión opcional Long <-> BigDecimal
    default BigDecimal map(Long value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }

    default Long map(BigDecimal value) {
        return value != null ? value.longValue() : null;
    }
}
