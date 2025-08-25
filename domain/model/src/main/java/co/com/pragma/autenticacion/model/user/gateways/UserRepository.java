package co.com.pragma.autenticacion.model.user.gateways;

import co.com.pragma.autenticacion.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Puerto (interfaz) del repositorio de usuarios.
 *
 * 🔹 Define las operaciones que el sistema necesita para manipular usuarios.
 * 🔹 Hace parte del dominio y sigue el principio de inversión de dependencias (Clean Architecture).
 */
public interface UserRepository {

    /**
     * Guardar un usuario nuevo.
     * @param user objeto usuario a guardar.
     * @return Mono<User> con el usuario guardado.
     */
    Mono<User> saveUser(User user);

    /**
     * Obtener todos los usuarios del sistema.
     * @return Flux<User> con la lista reactiva de usuarios.
     */
    Flux<User> getAllUsers();

    /**
     * Obtener un usuario por su número de identificación.
     * @param number número único de identificación.
     * @return Mono<User> con el usuario encontrado o vacío.
     */
    Mono<User> getUserByIdNumber(Long number);

    /**
     * Editar un usuario existente.
     * @param user objeto usuario con cambios.
     * @return Mono<User> actualizado.
     */
    Mono<User> editUser(User user);

    /**
     * Eliminar un usuario por su número de identificación.
     * @param idNumber número de identificación.
     * @return Mono<Void> indicando éxito o error.
     */
    Mono<Void> deleteUser(Long idNumber);

    /**
     * Validar si ya existe un usuario con el correo indicado.
     * @param email correo electrónico.
     * @return Mono<Boolean> true si ya existe.
     */
    Mono<Boolean> existsByEmail(String email);

    /**
     * Validar si ya existe un usuario con el documento de identidad indicado.
     * @param documentoIdentidad número de documento.
     * @return Mono<Boolean> true si ya existe.
     */
    Mono<Boolean> existsByDocumento(String documentoIdentidad);

    /**
     * Verificar si existe un rol asociado al usuario mediante su id.
     * @param idRol identificador del rol.
     * @return Mono<Boolean> true si el rol existe.
     */
    Mono<Boolean> existsRoleById(BigDecimal idRol);

}
