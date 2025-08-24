package co.com.pragma.autenticacion.r2dbc.mapper;


import co.com.pragma.autenticacion.model.rol.Rol;
import co.com.pragma.autenticacion.r2dbc.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", implementationName = "RolMapperImpl")
public interface RolMapper {

    @Mapping(target = "idRol", source = "uniqueId")
    RoleEntity toEntity(Rol rol);

    @Mapping(target = "uniqueId", source = "idRol")
    Rol toModel(RoleEntity roleEntity);

    // Métodos auxiliares para conversión Long <-> Integer
    default Long map(Integer value) {
        return value != null ? value.longValue() : null;
    }

    default Integer map(Long value) {
        return value != null ? value.intValue() : null;
    }
}