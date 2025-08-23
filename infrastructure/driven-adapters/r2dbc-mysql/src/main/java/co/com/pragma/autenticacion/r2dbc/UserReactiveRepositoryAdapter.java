package co.com.pragma.autenticacion.r2dbc;

import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.model.user.gateways.UserRepository;
import co.com.pragma.autenticacion.r2dbc.entity.UserEntity;
import co.com.pragma.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.autenticacion.r2dbc.mapper.UsuarioR2dbcMapper;
import co.com.pragma.autenticacion.usecase.exceptions.UserNotFoundException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public class UserReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<User, UserEntity, Long, UsuarioReactiveRepository>
        implements UserRepository {

    private final UsuarioR2dbcMapper usuarioMapper;

    public UserReactiveRepositoryAdapter(UsuarioReactiveRepository repository,
                                         UsuarioR2dbcMapper usuarioMapper) {
        super(repository, null, usuarioMapper::toModel); // mapper: UserEntity -> User
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public Mono<User> saveUser(User user) {
        // Convierte User -> UserEntity y guarda, luego mapea de vuelta
        UserEntity entity = usuarioMapper.toEntity(user);
        return super.repository.save(entity)
                .map(usuarioMapper::toModel);
    }

    @Override
    public Flux<User> getAllUsers() {
        return super.repository.findAll()
                .map(usuarioMapper::toModel);
    }

    @Override
    public Mono<User> getUserByIdNumber(Long idNumber) {
        return super.repository.findById(idNumber)
                .map(usuarioMapper::toModel)
                .switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    @Override
    public Mono<User> editUser(User user) {
        return super.repository.findById(user.getIdNumber())
                .flatMap(existing -> {
                    UserEntity updated = usuarioMapper.toEntity(user);
                    return super.repository.save(updated);
                })
                .map(usuarioMapper::toModel)
                .switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    @Override
    public Mono<Void> deleteUser(Long idNumber) {
        return super.repository.deleteById(idNumber);
    }
}