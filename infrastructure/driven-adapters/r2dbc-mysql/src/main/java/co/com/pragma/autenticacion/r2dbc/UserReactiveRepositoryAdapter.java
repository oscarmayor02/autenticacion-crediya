package co.com.pragma.autenticacion.r2dbc;

import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.model.user.gateways.UserRepository;
import co.com.pragma.autenticacion.r2dbc.entity.UserEntity;
import co.com.pragma.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.autenticacion.r2dbc.mapper.UsuarioR2dbcMapper;
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
        extends ReactiveAdapterOperations<User, UserEntity, Long, UsuarioReactiveRepository>
        implements UserRepository {

    private final UsuarioReactiveRepository usuarioReactiveRepository;
    private final UsuarioR2dbcMapper usuarioMapper;
    private final TransactionalOperator transactionalOperator;

    /**
     * Constructor: inyecta repositorio, mapper y operador transaccional.
     */
    public UserReactiveRepositoryAdapter(UsuarioReactiveRepository repository,
                                         UsuarioR2dbcMapper usuarioMapper,
                                         TransactionalOperator transactionalOperator) {
        super(repository, null, usuarioMapper::toModel);
        this.usuarioReactiveRepository = repository;
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
            return usuarioReactiveRepository.save(entity)
                    .map(usuarioMapper::toModel);
        }).as(transactionalOperator::transactional);
    }

    /**
     * Obtiene todos los usuarios.
     * - Operación de solo lectura, no requiere transacción.
     */
    @Override
    public Flux<User> getAllUsers() {
        return usuarioReactiveRepository.findAll()
                .map(usuarioMapper::toModel);
    }

    /**
     * Busca un usuario por ID.
     * - Si no existe, lanza NotFoundException.
     */
    @Override
    public Mono<User> getUserByIdNumber(Long idNumber) {
        return usuarioReactiveRepository.findById(idNumber)
                .map(usuarioMapper::toModel)
                .switchIfEmpty(Mono.error(new NotFoundException("Usuario no encontrado con id: " + idNumber)));
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
        return usuarioReactiveRepository.findById(user.getIdNumber())
                .switchIfEmpty(Mono.error(new NotFoundException("No se pudo actualizar, usuario no encontrado")))
                .flatMap(existing -> {
                    UserEntity updated = usuarioMapper.toEntity(user);
                    updated.setIdUsuario(existing.getIdUsuario()); // preserva el ID generado en la BD
                    return usuarioReactiveRepository.save(updated)
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
        return usuarioReactiveRepository.deleteById(idNumber)
                .as(transactionalOperator::transactional);
    }

    /**
     * Verifica si ya existe un usuario con un correo electrónico dado.
     */
    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return usuarioReactiveRepository.existsByCorreoElectronico(email);
    }

    /**
     * Verifica si ya existe un usuario con un documento de identidad dado.
     */
    @Override
    public Mono<Boolean> existsByDocumento(String documentoIdentidad) {
        return usuarioReactiveRepository.existsByDocumentoIdentidad(documentoIdentidad);
    }

    /**
     * Verifica si un rol existe dado un ID.
     */
    @Override
    public Mono<Boolean> existsRoleById(BigDecimal idRol) {
        return usuarioReactiveRepository.existsByRolId(idRol.longValue());
    }
}
