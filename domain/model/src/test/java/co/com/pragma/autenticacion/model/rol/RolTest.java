package co.com.pragma.autenticacion.model.rol;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad Rol.
 *
 * 🔹 Validan que los constructores, builder y setters funcionan correctamente.
 */
class RolTest {

    @Test
    void shouldCreateRolUsingBuilder() {
        // Crear un rol usando el builder
        Rol rol = Rol.builder()
                .uniqueId(1)
                .nombre("ADMIN")
                .descripcion("Administrador del sistema")
                .build();

        // Validaciones
        assertEquals(1, rol.getUniqueId());
        assertEquals("ADMIN", rol.getNombre());
        assertEquals("Administrador del sistema", rol.getDescripcion());
    }

    @Test
    void shouldModifyRolUsingSetters() {
        Rol rol = new Rol();

        rol.setUniqueId(2);
        rol.setNombre("USER");
        rol.setDescripcion("Usuario estándar");

        assertEquals(2, rol.getUniqueId());
        assertEquals("USER", rol.getNombre());
        assertEquals("Usuario estándar", rol.getDescripcion());
    }

    @Test
    void shouldSupportAllArgsConstructor() {
        Rol rol = new Rol(3, "GUEST", "Usuario invitado");

        assertEquals(3, rol.getUniqueId());
        assertEquals("GUEST", rol.getNombre());
        assertEquals("Usuario invitado", rol.getDescripcion());
    }
}
