package co.com.pragma.autenticacion.model.auth;

import lombok.experimental.UtilityClass;

/**
 * Clase de constantes para autenticación, seguridad y validaciones.
 * Centraliza todos los valores fijos usados en el dominio.
 */
@UtilityClass
public final class AuthConstants {

    // ------------------ Headers / Bearer ------------------
    public static final String BEARER_PREFIX = "Bearer ";

    // ------------------ Claims ------------------
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_ID    = "uid";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_NAME  = "name";

    // ------------------ Roles ------------------
    public static final String ADMIN_ROLE   = "ADMIN";
    public static final String ADVISOR_ROLE = "ASESOR";
    public static final String CLIENT_ROLE  = "CLIENTE";

    // ------------------ Tiempos (ms) ------------------
    public static final long ACCESS_TOKEN_TTL_MS  = 60_000L;       // 1 minuto
    public static final long REFRESH_TOKEN_TTL_MS = 15 * 60_000L;  // 15 minutos

    // ------------------ Endpoints Auth ------------------
    public static final String LOGIN_PATH   = "/api/v1/login";
    public static final String REFRESH_PATH = "/api/v1/token/refresh";

    // ------------------ Endpoints Usuarios ------------------
    public static final String USERS_BASE_PATH     = "/api/v1/usuarios";
    public static final String PATH_ID = "/{id}";
    public static final String PATH_EXISTS_EMAIL = "/exists/email/{email}";
    public static final String PATH_EXISTS_DOC = "/exists/documento/{documento}";

    // ------------------ Endpoints  ------------------
    public static final String ROLE_BASE_PATH = "/api/v1/roles";
    public static final String ROLE_BY_ID_PATH = "/{uniqueId}";

    // ------------------ USER MGS SWAGGER ------------------
    public static final String MSG_NAME_SWAGGER = "Usuarios API";
    public static final String MSG_DELETE_USER = "Eliminar un usuario por ID";
    public static final String MSG_DELETE = "deleteUser";
    public static final String MSG_GET_ALL_USERS = "Obtener todos los usuarios";
    public static final String MSG_GET = "getAllUsers";
    public static final String MSG_UPDATE_USER = "Usuario actualizado";
    public static final String MSG_UPDATE = "updateUser";
    public static final String MSG_USER_OK_UPDATE = "Editar un usuario existente";
    public static final String MSG_REGISTER_USER = "registrarUsuario";
    public static final String MSG_CREATE_USER = "Crear un nuevo usuario";
    public static final String MSG_USER_CRUD = "Operaciones CRUD para Usuarios";
    public static final String MSG_GET_USER_DESCRIPTION= "Lista de usuarios";
    public static final String MSG_GET_USER_BY_ID = "getUserById";
    public static final String MSG_GET_USER_BY_ID_DESCRIPTION= "Obtener un usuario por ID";


    // ------------------ Mensajes de Error ------------------
    public static final String MSG_INVALID_CREDENTIALS = "Credenciales inválidas";
    public static final String MSG_INVALID_TOKEN       = "Token inválido";
    public static final String MSG_INTERNAL_ERROR      = "Error interno del servidor";
    public static final String MSG_USER_NOT_FOUND      = "Usuario no encontrado";
    public static final String MSG_DUPLICATE_EMAIL     = "El correo ya está registrado";
    public static final String MSG_DUPLICATE_DOCUMENT  = "El documento ya está registrado";
    public static final String MGS_FIELD_REQUIRED      = "email y password son obligatorios";
    public static final String MSG_BODY_REQUIRED      = "Body requerido";
    public static final String MSG_INVALID_DELETE_USER      = "Error eliminando usuario: {}";
    public static final String VALIDATION_EMAIL     = "Error verificando email: {}";
    public static final String VALIDATION_DOCUMENT_INVALID        = "Error verificando documento: {}";
    public static final String MSG_INVALID_EDIT_USER        = "Error editando usuario: {}";
    public static final String MSG_INVALID_GET_USER        = "Error obteniendo usuario: {}";
    public static final String MSG_INVALID_CREATE_USER     = "Error creando usuario: {}";
    public static final String MSG_INVALID_CREATE_ROL = "Error creando rol: {}";


    // ------------------ Validaciones Usuario ------------------
    public static final String VALIDATION_NAME_REQUIRED       = "El nombre no puede estar vacío";
    public static final String VALIDATION_LASTNAME_REQUIRED   = "El apellido no puede estar vacío";
    public static final String VALIDATION_EMAIL_REQUIRED      = "El correo electrónico es obligatorio";
    public static final String VALIDATION_EMAIL_FORMAT        = "Formato de correo inválido";
    public static final String VALIDATION_SALARY_REQUIRED     = "El salario base es obligatorio";
    public static final String VALIDATION_SALARY_RANGE        = "El salario base debe estar entre 0 y 1.5000.000";
    public static final String VALIDATION_DOB_REQUIRED        = "La fecha de nacimiento es obligatoria";
    public static final String VALIDATION_DOB_FORMAT          = "Formato de fecha inválido. Debe ser yyyy-MM-dd";
    public static final String VALIDATION_DOB_UNDERAGE        = "El usuario debe ser mayor de edad (18 años o más)";
    public static final String VALIDATION_PASSWORD_REQUIRED  = "La contraseña es obligatoria";
    public static final String VALIDATION_USER_NOT_FOUND_ID   = "Usuario no encontrado con id: ";
    public static final String VALIDATION_USER_NOT_FOUND_UPDATE= "No se pudo actualizar, usuario no encontrado";
    public static final String VALIDATION_LOGIN_SUCCESS= "Login exitoso";
    public static final String VALIDATION_REFRESH_SUCCESS= "Refresh exitoso";

    // ------------------ Mensajes de Éxito Usuario ------------------
    public static final String MSG_USER_DELETE_OK  = "Usuario eliminado con id: {}";
    public static final String MSG_USER_UPDATE_OK  = "Usuario actualizado con id: {}";
    public static final String MSG_USER_GET_OK  = "Se consultaron todos los usuarios";
    public static final String MGS_USER_CREATE_OK  ="Usuario creado exitosamente: {}";
    public static final String MSG_USER_FOUND_BY_ID  = "Usuario encontrado";

    // ------------------ Validaciones Roles ------------------
    public static final String VALIDATION_ROLE_CREATE = "Role creado exitosamente: {}";
}
