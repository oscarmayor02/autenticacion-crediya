package co.com.pragma.autenticacion.api.mapper;

import co.com.pragma.autenticacion.api.dto.UserRequestDTO;
import co.com.pragma.autenticacion.api.dto.UserResponseDTO;
import co.com.pragma.autenticacion.model.user.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserApiMapper {

    @Mapping(target = "idNumber", ignore = true)
    @Mapping(target = "idRole", expression = "java(dto.getRoleId() != null ? BigDecimal.valueOf(dto.getRoleId()) : null)")
    User toDomain(UserRequestDTO dto);

    @Mapping(target = "roleId", expression = "java(user.getIdRole() != null ? user.getIdRole().intValue() : null)")
    UserRequestDTO toDTO(User user);

    // Mapeo a respuesta segura (no incluye password ni email)
    @Mapping(target = "role", expression = "java(new UserResponseDTO.RolResponseDTO(user.getIdRole().intValue(), mapRoleName(user.getIdRole().intValue())))")
    UserResponseDTO toResponseDTO(User user);

    // Método auxiliar para traducir roleId → nombre del rol
    default String mapRoleName(Integer roleId) {
        if (roleId == null) return null;
        switch (roleId) {
            case 1: return "ADMIN";
            case 2: return "ASESOR";
            case 3: return "CLIENTE";
            default: return "DESCONOCIDO";
        }
    }
}



