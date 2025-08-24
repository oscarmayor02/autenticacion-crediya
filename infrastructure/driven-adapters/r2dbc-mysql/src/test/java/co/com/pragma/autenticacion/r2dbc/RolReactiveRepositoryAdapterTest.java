package co.com.pragma.autenticacion.r2dbc;


import co.com.pragma.autenticacion.model.rol.Rol;
import co.com.pragma.autenticacion.r2dbc.entity.RoleEntity;
import co.com.pragma.autenticacion.r2dbc.mapper.RolMapper;
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
class RolReactiveRepositoryAdapterTest {

    @Mock
    private RolReactiveRepository repository; // Mock del repositorio reactivo

    @Mock
    private RolMapper mapper; // Mock del mapper Rol <-> RoleEntity

    @InjectMocks
    private RolReactiveRepositoryAdapter adapter; // Adapter a probar

    private Rol rol;
    private RoleEntity entity;

    @BeforeEach
    void setUp() {
        // Creamos un rol de ejemplo
        rol = Rol.builder()
                .uniqueId(1)
                .nombre("ADMIN")
                .descripcion("Administrador del sistema")
                .build();

        // Creamos la entidad correspondiente
        entity = RoleEntity.builder()
                .idRol(1L)
                .nombre("ADMIN")
                .descripcion("Administrador del sistema")
                .build();
    }

    @Test
    void findById_shouldReturnRol() {
        // Configuramos comportamiento de los mocks
        when(repository.findById(1)).thenReturn(Mono.just(entity));
        when(mapper.toModel(entity)).thenReturn(rol);

        // Ejecutamos método
        StepVerifier.create(adapter.findById(1L))
                .expectNext(rol)
                .verifyComplete();

        verify(repository, times(1)).findById(1);
    }

    @Test
    void save_shouldMapAndCallRepo() {
        when(mapper.toEntity(rol)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.toModel(entity)).thenReturn(rol);

        StepVerifier.create(adapter.save(rol))
                .expectNext(rol)
                .verifyComplete();

        verify(repository, times(1)).save(entity);
    }

    @Test
    void update_shouldModifyExistingRol() {
        Rol updatedRol = Rol.builder()
                .uniqueId(1)
                .nombre("SUPER_ADMIN")
                .descripcion("Administrador principal")
                .build();

        RoleEntity updatedEntity = RoleEntity.builder()
                .idRol(1L)
                .nombre("SUPER_ADMIN")
                .descripcion("Administrador principal")
                .build();

        when(repository.findById(1)).thenReturn(Mono.just(entity)); // encuentra rol existente
        when(mapper.toEntity(updatedRol)).thenReturn(updatedEntity);
        when(repository.save(updatedEntity)).thenReturn(Mono.just(updatedEntity));
        when(mapper.toModel(updatedEntity)).thenReturn(updatedRol);

        StepVerifier.create(adapter.update(updatedRol))
                .expectNext(updatedRol)
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