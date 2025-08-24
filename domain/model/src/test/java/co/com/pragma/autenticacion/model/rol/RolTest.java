package co.com.pragma.autenticacion.model.rol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RolTest {

    @Test
    void shouldCreateRolUsingBuilder() {
        // Creamos un Rol usando el builder
        Rol rol = Rol.builder()
                .uniqueId(1)
                .nombre("ADMIN")
                .descripcion("Administrador del sistema")
                .build();

        // Validamos que los valores se asignaron correctamente
        assertEquals(1, rol.getUniqueId());
        assertEquals("ADMIN", rol.getNombre());
        assertEquals("Administrador del sistema", rol.getDescripcion());
    }

    @Test
    void shouldModifyRolUsingSetters() {
        // Creamos un rol vacío
        Rol rol = new Rol();

        // Asignamos valores con setters
        rol.setUniqueId(2);
        rol.setNombre("USER");
        rol.setDescripcion("Usuario estándar");

        // Validamos que se asignaron correctamente
        assertEquals(2, rol.getUniqueId());
        assertEquals("USER", rol.getNombre());
        assertEquals("Usuario estándar", rol.getDescripcion());
    }

    @Test
    void shouldSupportAllArgsConstructor() {
        // Creamos un rol con el constructor completo
        Rol rol = new Rol(3, "GUEST", "Usuario invitado");

        // Validamos los valores
        assertEquals(3, rol.getUniqueId());
        assertEquals("GUEST", rol.getNombre());
        assertEquals("Usuario invitado", rol.getDescripcion());
    }
}