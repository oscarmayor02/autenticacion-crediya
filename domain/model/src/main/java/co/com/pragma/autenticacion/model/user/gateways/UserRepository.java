    package co.com.pragma.autenticacion.model.user.gateways;

    import co.com.pragma.autenticacion.model.user.User;
    import reactor.core.publisher.Flux;
    import reactor.core.publisher.Mono;

    import java.math.BigDecimal;

    public interface UserRepository {
        Mono<User> saveUser(User user);
        Flux<User> getAllUsers();
        Mono<User> getUserByIdNumber(Long number);
        Mono<User> editUser(User user);
        Mono<Void> deleteUser(Long idNumber);
        Mono<Boolean> existsByEmail(String email);
        Mono<Boolean> existsByDocumento(String documentoIdentidad);
        Mono<Boolean> existsRoleById(BigDecimal idRol);

    }
