package co.com.pragma.autenticacion.r2dbc.mapper;


import co.com.pragma.autenticacion.model.role.Role;
import co.com.pragma.autenticacion.r2dbc.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", implementationName = "RoleMapperImpl")
public interface RoleMapper {

    @Mapping(target = "idRole", source = "uniqueId")
    RoleEntity toEntity(Role role);

    @Mapping(target = "uniqueId", source = "idRole")
    Role toModel(RoleEntity roleEntity);

    // Métodos auxiliares para conversión Long <-> Integer
    default Long map(Integer value) {
        return value != null ? value.longValue() : null;
    }

    default Integer map(Long value) {
        return value != null ? value.intValue() : null;
    }
}