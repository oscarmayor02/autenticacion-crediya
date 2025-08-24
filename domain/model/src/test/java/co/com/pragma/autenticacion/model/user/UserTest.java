package co.com.pragma.autenticacion.model.user;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserUsingBuilder() {
        // Creamos un usuario usando el builder
        User user = User.builder()
                .idNumber(12345L)
                .nombre("Juan")
                .apellido("Pérez")
                .fechaNacimiento("1990-01-01")
                .direccion("Calle 123")
                .telefono("3001234567")
                .correoElectronico("juan.perez@test.com")
                .salarioBase(BigDecimal.valueOf(2500000))
                .documentoIdentidad("987654321")
                .idRol(BigDecimal.valueOf(1))
                .build();

        // Validamos los valores
        assertEquals(12345L, user.getIdNumber());
        assertEquals("Juan", user.getNombre());
        assertEquals("Pérez", user.getApellido());
        assertEquals("1990-01-01", user.getFechaNacimiento());
        assertEquals("Calle 123", user.getDireccion());
        assertEquals("3001234567", user.getTelefono());
        assertEquals("juan.perez@test.com", user.getCorreoElectronico());
        assertEquals(BigDecimal.valueOf(2500000), user.getSalarioBase());
        assertEquals("987654321", user.getDocumentoIdentidad());
        assertEquals(BigDecimal.valueOf(1), user.getIdRol());
    }

    @Test
    void shouldModifyUserUsingSetters() {
        User user = new User();
        user.setIdNumber(123L);
        user.setNombre("Carlos");
        user.setApellido("Lopez");

        assertEquals(123L, user.getIdNumber());
        assertEquals("Carlos", user.getNombre());
        assertEquals("Lopez", user.getApellido());
    }

    @Test
    void shouldSupportAllArgsConstructor() {
        User user = new User(456L, "Ana", "Martínez", "1985-05-05",
                "Carrera 45", "3100000000", "ana@test.com",
                BigDecimal.valueOf(3500000), "11223344",
                BigDecimal.valueOf(2));

        assertEquals(456L, user.getIdNumber());
        assertEquals("Ana", user.getNombre());
    }
}