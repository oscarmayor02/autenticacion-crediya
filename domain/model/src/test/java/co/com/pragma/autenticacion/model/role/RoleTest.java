package co.com.pragma.autenticacion.model.role;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad Role.
 *
 * 🔹 Validan que los constructores, builder y setters funcionan correctamente.
 */
class RoleTest {

    @Test
    void shouldCreateRolUsingBuilder() {
        // Crear un role usando el builder
        Role role = Role.builder()
                .uniqueId(1)
                .name("ADMIN")
                .description("Administrador del sistema")
                .build();

        // Validaciones
        assertEquals(1, role.getUniqueId());
        assertEquals("ADMIN", role.getName());
        assertEquals("Administrador del sistema", role.getDescription());
    }

    @Test
    void shouldModifyRolUsingSetters() {
        Role role = new Role();

        role.setUniqueId(2);
        role.setName("USER");
        role.setDescription("Usuario estándar");

        assertEquals(2, role.getUniqueId());
        assertEquals("USER", role.getName());
        assertEquals("Usuario estándar", role.getDescription());
    }

    @Test
    void shouldSupportAllArgsConstructor() {
        Role role = new Role(3, "GUEST", "Usuario invitado");

        assertEquals(3, role.getUniqueId());
        assertEquals("GUEST", role.getName());
        assertEquals("Usuario invitado", role.getDescription());
    }
}
