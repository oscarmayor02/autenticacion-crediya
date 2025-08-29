package co.com.pragma.autenticacion.r2dbc;


import co.com.pragma.autenticacion.model.role.Role;
import co.com.pragma.autenticacion.r2dbc.entity.RoleEntity;
import co.com.pragma.autenticacion.r2dbc.mapper.RoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Configura JUnit 5 con Mockito
class RoleReactiveRepositoryAdapterTest {

    @Mock
    private RoleReactiveRepository repository; // Mock del repositorio reactivo

    @Mock
    private RoleMapper mapper; // Mock del mapper Role <-> RoleEntity

    @InjectMocks
    private RoleReactiveRepositoryAdapter adapter; // Adapter a probar

    private Role role;
    private RoleEntity entity;

    @BeforeEach
    void setUp() {
        // Creamos un role de ejemplo
        role = Role.builder()
                .uniqueId(1)
                .name("ADMIN")
                .descripcion("Administrador del sistema")
                .build();

        // Creamos la entidad correspondiente
        entity = RoleEntity.builder()
                .idRole(1L)
                .name("ADMIN")
                .description("Administrador del sistema")
                .build();
    }

    @Test
    void findById_shouldReturnRol() {
        // Configuramos comportamiento de los mocks
        when(repository.findById(1)).thenReturn(Mono.just(entity));
        when(mapper.toModel(entity)).thenReturn(role);

        // Ejecutamos método
        StepVerifier.create(adapter.findById(1L))
                .expectNext(role)
                .verifyComplete();

        verify(repository, times(1)).findById(1);
    }

    @Test
    void save_shouldMapAndCallRepo() {
        when(mapper.toEntity(role)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.toModel(entity)).thenReturn(role);

        StepVerifier.create(adapter.save(role))
                .expectNext(role)
                .verifyComplete();

        verify(repository, times(1)).save(entity);
    }

    @Test
    void update_shouldModifyExistingRol() {
        Role updatedRole = Role.builder()
                .uniqueId(1)
                .name("SUPER_ADMIN")
                .descripcion("Administrador principal")
                .build();

        RoleEntity updatedEntity = RoleEntity.builder()
                .idRole(1L)
                .name("SUPER_ADMIN")
                .description("Administrador principal")
                .build();

        when(repository.findById(1)).thenReturn(Mono.just(entity)); // encuentra role existente
        when(mapper.toEntity(updatedRole)).thenReturn(updatedEntity);
        when(repository.save(updatedEntity)).thenReturn(Mono.just(updatedEntity));
        when(mapper.toModel(updatedEntity)).thenReturn(updatedRole);

        StepVerifier.create(adapter.update(updatedRole))
                .expectNext(updatedRole)
                .verifyComplete();

        verify(repository, times(1)).findById(1);
        verify(repository, times(1)).save(updatedEntity);
    }

    @Test
    void delete_shouldCallRepo() {
        when(repository.deleteById(1)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.delete(1L))
                .verifyComplete();

        verify(repository, times(1)).deleteById(1);
    }
}