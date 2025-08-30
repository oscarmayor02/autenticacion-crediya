package co.com.pragma.autenticacion.r2dbc;

import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.r2dbc.entity.UserEntity;
import co.com.pragma.autenticacion.r2dbc.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    private static final Long USER_ID = 1L;
    private static final BigDecimal ROLE_ID = BigDecimal.ONE;

    @Mock
    private UserReactiveRepository repo;

    @Mock
    private UserMapper mapper;

    @Mock
    private TransactionalOperator tx;

    @InjectMocks
    private UserReactiveRepositoryAdapter adapter;

    private User baseUser;
    private UserEntity baseEntity;

    @BeforeEach
    void setUp() {
        baseUser = buildUser(USER_ID, "Ana", "Martinez");
        baseEntity = buildEntity(USER_ID, "Ana", "Martinez");
    }

    /** Factory Methods **/
    private User buildUser(Long id, String name, String lastName) {
        return User.builder()
                .idNumber(id)
                .name(name)
                .lastName(lastName)
                .dateOfBirth("1990-01-01")
                .address("Calle 1")
                .telephone("3000000")
                .email("ana@test.com")
                .baseSalary(BigDecimal.valueOf(2000))
                .identityDocument("123")
                .idRole(ROLE_ID)
                .password("secret")
                .build();
    }

    private UserEntity buildEntity(Long id, String name, String lastName) {
        return UserEntity.builder()
                .idUser(id)
                .name(name)
                .lastName(lastName)
                .dateOfBirth("1990-01-01")
                .address("Calle 1")
                .telephone("3000000")
                .email("ana@test.com")
                .baseSalary(BigDecimal.valueOf(2000))
                .identityDocument("123")
                .roleId(ROLE_ID)
                .password("secret")
                .build();
    }

    /** Helper para simular transacciones **/
    private void mockTransactional(Mono<?> mono) {
        when(tx.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void saveUser_shouldReturnSavedUser() {
        mockTransactional(Mono.just(baseUser));
        when(mapper.toEntity(baseUser)).thenReturn(baseEntity);
        when(repo.save(baseEntity)).thenReturn(Mono.just(baseEntity));
        when(mapper.toModel(baseEntity)).thenReturn(baseUser);

        StepVerifier.create(adapter.saveUser(baseUser))
                .expectNext(baseUser)
                .verifyComplete();

        verify(repo).save(baseEntity);
    }

    @Test
    void getUserById_shouldReturnUser() {
        when(repo.findById(USER_ID)).thenReturn(Mono.just(baseEntity));
        when(mapper.toModel(baseEntity)).thenReturn(baseUser);

        StepVerifier.create(adapter.getUserByIdNumber(USER_ID))
                .expectNext(baseUser)
                .verifyComplete();

        verify(repo).findById(USER_ID);
    }

    @Test
    void getUserById_shouldThrowNotFoundException() {
        when(repo.findById(2L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.getUserByIdNumber(2L))
                .expectErrorMatches(ex -> ex instanceof co.com.pragma.autenticacion.usecase.exceptions.NotFoundException
                        && ex.getMessage().contains("Usuario no encontrado"))
                .verify();

        verify(repo).findById(2L);
    }

    @Test
    void editUser_shouldUpdateUser() {
        User updatedUser = baseUser.toBuilder().lastName("Lopez").build();
        UserEntity updatedEntity = buildEntity(USER_ID, "Ana", "Lopez");

        when(repo.findById(USER_ID)).thenReturn(Mono.just(baseEntity));
        when(mapper.toEntity(updatedUser)).thenReturn(updatedEntity);
        when(repo.save(updatedEntity)).thenReturn(Mono.just(updatedEntity));
        when(mapper.toModel(updatedEntity)).thenReturn(updatedUser);
        mockTransactional(Mono.just(updatedUser));

        StepVerifier.create(adapter.editUser(updatedUser))
                .expectNext(updatedUser)
                .verifyComplete();

        verify(repo).findById(USER_ID);
        verify(repo).save(updatedEntity);
    }

    @Test
    void deleteUser_shouldCompleteWithoutError() {
        when(repo.deleteById(USER_ID)).thenReturn(Mono.empty());
        mockTransactional(Mono.empty());

        StepVerifier.create(adapter.deleteUser(USER_ID))
                .verifyComplete();

        verify(repo).deleteById(USER_ID);
    }
}
