package co.com.pragma.autenticacion.api.mapper;

import co.com.pragma.autenticacion.api.dto.UsuarioRequestDTO;
import co.com.pragma.autenticacion.model.user.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioApiMapper {

    @Mapping(target = "idNumber", ignore = true) // Generado por la BD
    @Mapping(target = "documentoIdentidad", source = "documentoIdentidad")
    @Mapping(target = "fechaNacimiento", source = "fecha_nacimiento")
    @Mapping(target = "correoElectronico", source = "email")
    @Mapping(target = "salarioBase", source = "salarioBase")
    @Mapping(target = "idRol", expression = "java(dto.getRolId() != null ? BigDecimal.valueOf(dto.getRolId()) : null)")
    User toDomain(UsuarioRequestDTO dto);

    @Mapping(target = "documentoIdentidad", source = "documentoIdentidad")
    @Mapping(target = "fecha_nacimiento", source = "fechaNacimiento")
    @Mapping(target = "email", source = "correoElectronico")
    @Mapping(target = "salarioBase", source = "salarioBase")
    @Mapping(target = "rolId", expression = "java(user.getIdRol() != null ? user.getIdRol().intValue() : null)")
    UsuarioRequestDTO toDTO(User user);
}


