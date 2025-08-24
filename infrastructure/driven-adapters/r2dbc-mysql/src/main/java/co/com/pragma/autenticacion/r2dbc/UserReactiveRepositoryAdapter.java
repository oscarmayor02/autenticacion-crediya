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
 * Implementación del repositorio reactivo para la entidad User.
 * Utiliza Spring Data R2DBC y aplica patrón Adapter para conectar dominio con infraestructura.
 * Todas las operaciones críticas se manejan dentro de transacciones reactivas.
 */
@Repository
public class UserReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<User, UserEntity, Long, UsuarioReactiveRepository>
        implements UserRepository {

    private final UsuarioReactiveRepository usuarioReactiveRepository; // Repositorio reactivo que interactúa con la DB
    private final UsuarioR2dbcMapper usuarioMapper; // Mapper para convertir entre User (dominio) y UserEntity (persistencia)
    private final TransactionalOperator transactionalOperator; // Maneja transacciones reactivas

    /**
     * Constructor: inyecta dependencias principales.
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
     * Guarda un usuario en la base de datos.
     * Se ejecuta dentro de una transacción reactiva.
     */
    @Override
    public Mono<User> saveUser(User user) {
        return Mono.defer(() -> {
            // Convierte el modelo de dominio a entidad persistente
            UserEntity entity = usuarioMapper.toEntity(user);
            // Guarda la entidad y la convierte nuevamente a modelo
            return usuarioReactiveRepository.save(entity)
                    .map(usuarioMapper::toModel);
        }).as(transactionalOperator::transactional); // Aplica transacción reactiva
    }

    /**
     * Obtiene todos los usuarios de la base de datos.
     * Esta operación es de solo lectura, no requiere transacción.
     */
    @Override
    public Flux<User> getAllUsers() {
        return usuarioReactiveRepository.findAll()
                .map(usuarioMapper::toModel);
    }

    /**
     * Busca un usuario por su ID.
     * Si no lo encuentra, lanza NotFoundException.
     */
    @Override
    public Mono<User> getUserByIdNumber(Long idNumber) {
        return usuarioReactiveRepository.findById(idNumber)
                .map(usuarioMapper::toModel)
                .switchIfEmpty(Mono.error(new NotFoundException("Usuario no encontrado con id: " + idNumber)));
    }

    /**
     * Edita un usuario existente.
     * - Primero valida que el usuario exista.
     * - Luego actualiza los datos y guarda el nuevo estado.
     * Todo dentro de una transacción reactiva.
     */
    @Override
    public Mono<User> editUser(User user) {
        return usuarioReactiveRepository.findById(user.getIdNumber())
                .switchIfEmpty(Mono.error(new NotFoundException("No se pudo actualizar, usuario no encontrado")))
                .flatMap(existing -> {
                    // Convierte el modelo actualizado a entidad
                    UserEntity updated = usuarioMapper.toEntity(user);
                    // Mantiene el ID original de la base
                    updated.setIdUsuario(existing.getIdUsuario());
                    // Guarda cambios y devuelve el modelo
                    return usuarioReactiveRepository.save(updated)
                            .map(usuarioMapper::toModel);
                })
                .as(transactionalOperator::transactional);
    }

    /**
     * Elimina un usuario por su ID.
     * Envuelto en transacción reactiva para garantizar atomicidad si se añaden más operaciones.
     */
    @Override
    public Mono<Void> deleteUser(Long idNumber) {
        return usuarioReactiveRepository.deleteById(idNumber)
                .as(transactionalOperator::transactional);
    }

    /**
     * Verifica si existe un usuario con un correo electrónico específico.
     */
    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return usuarioReactiveRepository.existsByCorreoElectronico(email);
    }

    /**
     * Verifica si existe un usuario con un documento de identidad específico.
     */
    @Override
    public Mono<Boolean> existsByDocumento(String documentoIdentidad) {
        return usuarioReactiveRepository.existsByDocumentoIdentidad(documentoIdentidad);
    }

    /**
     * Verifica si existe un rol asociado a un ID dado.
     */
    @Override
    public Mono<Boolean> existsRoleById(BigDecimal idRol) {
        return usuarioReactiveRepository.existsByRolId(idRol.longValue());
    }
}