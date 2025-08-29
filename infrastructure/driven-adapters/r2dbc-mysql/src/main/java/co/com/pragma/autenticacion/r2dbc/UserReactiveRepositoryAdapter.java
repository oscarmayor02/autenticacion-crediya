package co.com.pragma.autenticacion.r2dbc;

import co.com.pragma.autenticacion.model.auth.AuthConstants;
import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.model.user.gateways.UserRepository;
import co.com.pragma.autenticacion.r2dbc.entity.UserEntity;
import co.com.pragma.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.autenticacion.r2dbc.mapper.UserMapper;
import co.com.pragma.autenticacion.usecase.exceptions.NotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Adaptador que implementa UserRepository del dominio.
 *
 * Explicación:
 * - Conecta la capa de dominio (User) con la base de datos (UserEntity).
 * - Usa Spring Data R2DBC para consultas reactivas.
 * - Incluye manejo transaccional con TransactionalOperator para garantizar atomicidad.
 */
@Repository
public class UserReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<User, UserEntity, Long, UserReactiveRepository>
        implements UserRepository {

    private final UserReactiveRepository userReactiveRepository;
    private final UserMapper usuarioMapper;
    private final TransactionalOperator transactionalOperator;

    /**
     * Constructor: inyecta repositorio, mapper y operador transaccional.
     */
    public UserReactiveRepositoryAdapter(UserReactiveRepository repository,
                                         UserMapper usuarioMapper,
                                         TransactionalOperator transactionalOperator) {
        super(repository, null, usuarioMapper::toModel);
        this.userReactiveRepository = repository;
        this.usuarioMapper = usuarioMapper;
        this.transactionalOperator = transactionalOperator;
    }

    /**
     * Guarda un usuario en la BD.
     * - Convierte el modelo de dominio a entidad.
     * - Lo guarda en la base.
     * - Lo convierte de nuevo a modelo.
     * - Todo dentro de una transacción reactiva.
     */
    @Override
    public Mono<User> saveUser(User user) {
        return Mono.defer(() -> {
            UserEntity entity = usuarioMapper.toEntity(user);
            return userReactiveRepository.save(entity)
                    .map(usuarioMapper::toModel);
        }).as(transactionalOperator::transactional);
    }

    /**
     * Obtiene todos los usuarios.
     * - Operación de solo lectura, no requiere transacción.
     */
    @Override
    public Flux<User> getAllUsers() {
        return userReactiveRepository.findAll()
                .map(usuarioMapper::toModel);
    }

    /**
     * Busca un usuario por ID.
     * - Si no existe, lanza NotFoundException.
     */
    @Override
    public Mono<User> getUserByIdNumber(Long idNumber) {
        return userReactiveRepository.findById(idNumber)
                .map(usuarioMapper::toModel)
                .switchIfEmpty(Mono.error(new NotFoundException(AuthConstants.VALIDATION_USER_NOT_FOUND_ID + idNumber)));
    }

    /**
     * Edita un usuario existente.
     * - Primero valida que el usuario exista en la BD.
     * - Convierte el nuevo modelo a entidad pero conserva el ID original.
     * - Guarda el nuevo estado y devuelve el modelo actualizado.
     * - Todo dentro de una transacción.
     */
    @Override
    public Mono<User> editUser(User user) {
        return userReactiveRepository.findById(user.getIdNumber())
                .switchIfEmpty(Mono.error(new NotFoundException(AuthConstants.VALIDATION_USER_NOT_FOUND_UPDATE)))
                .flatMap(existing -> {
                    UserEntity updated = usuarioMapper.toEntity(user);
                    updated.setIdUser(existing.getIdUser()); // preserva el ID generado en la BD
                    return userReactiveRepository.save(updated)
                            .map(usuarioMapper::toModel);
                })
                .as(transactionalOperator::transactional);
    }

    /**
     * Elimina un usuario por ID.
     * - Se ejecuta dentro de una transacción por consistencia.
     */
    @Override
    public Mono<Void> deleteUser(Long idNumber) {
        return userReactiveRepository.deleteById(idNumber)
                .as(transactionalOperator::transactional);
    }

    /**
     * Verifica si ya existe un usuario con un correo electrónico dado.
     */
    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return userReactiveRepository.existsByEmail(email);
    }

    /**
     * Verifica si ya existe un usuario con un documento de identidad dado.
     */
    @Override
    public Mono<Boolean> existsByDocument(String documentoIdentidad) {
        return userReactiveRepository.existsByIdentityDocument(documentoIdentidad);
    }

    /**
     * Verifica si un rol existe dado un ID.
     */
    @Override
    public Mono<Boolean> existsRoleById(BigDecimal idRol) {
        return userReactiveRepository.existsByRoleId(idRol.longValue());
    }

    @Override
    public Mono<User> getByEmail(String email) {
        return userReactiveRepository.findByEmail(email)
                .map(usuarioMapper::toModel);
    }
}
