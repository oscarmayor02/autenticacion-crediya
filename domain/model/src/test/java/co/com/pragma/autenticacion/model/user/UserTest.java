package co.com.pragma.autenticacion.model.user;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad User.
 *
 * 🔹 Aseguran que el builder, setters y constructores funcionan como se espera.
 */
class UserTest {

    @Test
    void shouldCreateUserUsingBuilder() {
        User user = User.builder()
                .idNumber(12345L)
                .name("Juan")
                .lastName("Pérez")
                .dateOfBirth("1990-01-01")
                .address("Calle 123")
                .telephone("3001234567")
                .email("juan.perez@test.com")
                .baseSalary(BigDecimal.valueOf(2500000))
                .identityDocument("987654321")
                .idRole(BigDecimal.valueOf(1))
                .build();

        assertEquals(12345L, user.getIdNumber());
        assertEquals("Juan", user.getName());
        assertEquals("Pérez", user.getLastName());
        assertEquals("1990-01-01", user.getDateOfBirth());
    }

    @Test
    void shouldModifyUserUsingSetters() {
        User user = new User();
        user.setIdNumber(123L);
        user.setName("Carlos");
        user.setLastName("Lopez");

        assertEquals(123L, user.getIdNumber());
        assertEquals("Carlos", user.getName());
        assertEquals("Lopez", user.getLastName());
    }

    @Test
    void shouldSupportAllArgsConstructor() {
        User user = new User(456L, "Ana", "Martínez", "1985-05-05",
                "Carrera 45", "3100000000", "ana@test.com",
                BigDecimal.valueOf(3500000), "11223344",
                BigDecimal.valueOf(2), "hashedPassword");

        assertEquals(456L, user.getIdNumber());
        assertEquals("Ana", user.getName());
    }
}
