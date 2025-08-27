package co.com.pragma.autenticacion.usecase.user;

import co.com.pragma.autenticacion.model.user.User;
import co.com.pragma.autenticacion.model.user.gateways.UserRepository;
import co.com.pragma.autenticacion.usecase.exceptions.DuplicateException;
import co.com.pragma.autenticacion.usecase.exceptions.NotFoundException;
import co.com.pragma.autenticacion.usecase.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Caso de uso para Usuarios.
 * - Contiene la lógica de negocio de CRUD.
 * - Aplica validaciones.
 * - Usa programación reactiva con Mono y Flux.
 */
@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    // ---------------------- CREATE ----------------------
    public Mono<User> saveUser(User user) {
        validateUser(user); // primero validamos negocio

        /**
         * Mono.zip: Ejecuta en paralelo 2 Monos y devuelve un Tuple (T1, T2).
         * - Aquí verificamos si ya existe el correo y el documento de forma concurrente.
         * - Si alguno ya existe → lanzamos DuplicateException.
         */
        return Mono.zip(
                userRepository.existsByEmail(user.getCorreoElectronico()),   // T1: ¿existe email?
                userRepository.existsByDocumento(user.getDocumentoIdentidad()) // T2: ¿existe documento?
        ).flatMap(tuple -> {
            boolean emailExists = tuple.getT1();
            boolean docExists = tuple.getT2();

            if (emailExists) return Mono.error(new DuplicateException("El correo ya está registrado"));
            if (docExists) return Mono.error(new DuplicateException("El documento ya está registrado"));

            // Si no hay duplicados → guardamos el usuario
            return userRepository.saveUser(user);
        });
    }

    // ---------------------- READ ----------------------
    public Flux<User> getAllUsers() {
        // Flux = flujo de N usuarios
        return userRepository.getAllUsers();
    }

    public Mono<User> getUserByIdNumber(Long idNumber) {
        // switchIfEmpty → si no encuentra nada, lanza NotFoundException
        return userRepository.getUserByIdNumber(idNumber)
                .switchIfEmpty(Mono.error(new NotFoundException("Usuario no encontrado con id: " + idNumber)));
    }

    // ---------------------- UPDATE ----------------------
    public Mono<User> editUser(User user) {
        return userRepository.getUserByIdNumber(user.getIdNumber())
                .switchIfEmpty(Mono.error(new NotFoundException("Usuario no encontrado con id: " + user.getIdNumber())))
                .flatMap(existing -> {
                    validateUser(user); // validamos antes de editar
                    return userRepository.editUser(user);
                });
    }

    // ---------------------- DELETE ----------------------
    public Mono<Void> deleteUser(Long idNumber) {
        return userRepository.getUserByIdNumber(idNumber)
                .switchIfEmpty(Mono.error(new NotFoundException("Usuario no encontrado con id: " + idNumber)))
                .flatMap(existing -> userRepository.deleteUser(idNumber));
    }

    // ---------------------- EXISTENCE CHECK ----------------------
    public Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Mono<Boolean> existsByDocumento(String documentoIdentidad) {
        return userRepository.existsByDocumento(documentoIdentidad);
    }

    // ---------------------- VALIDACIONES ----------------------
    private void validateUser(User user) {
        if (isNullOrEmpty(user.getNombre())) throw new ValidationException("El nombre no puede estar vacío");
        if (isNullOrEmpty(user.getApellido())) throw new ValidationException("El apellido no puede estar vacío");

        validateEmail(user.getCorreoElectronico());
        validateSalario(user.getSalarioBase());
        validateFechaNacimiento(user.getFechaNacimiento());
    }

    private void validateEmail(String email) {
        if (isNullOrEmpty(email)) throw new ValidationException("El correo electrónico es obligatorio");
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$"))
            throw new ValidationException("Formato de correo inválido");
    }

    private void validateSalario(BigDecimal salario) {
        if (salario == null) throw new ValidationException("El salario base es obligatorio");
        if (salario.compareTo(BigDecimal.ZERO) < 0 || salario.compareTo(new BigDecimal("15000000")) > 0)
            throw new ValidationException("El salario base debe estar entre 0 y 15.000.000");
    }

    private void validateFechaNacimiento(String fechaNacimiento) {
        if (isNullOrEmpty(fechaNacimiento)) throw new ValidationException("La fecha de nacimiento es obligatoria");
        try {
            LocalDate fecha = LocalDate.parse(fechaNacimiento, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int edad = Period.between(fecha, LocalDate.now()).getYears();
            if (edad < 18) throw new ValidationException("El usuario debe ser mayor de edad (18 años o más)");
        } catch (DateTimeParseException e) {
            throw new ValidationException("Formato de fecha inválido. Debe ser yyyy-MM-dd");
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
