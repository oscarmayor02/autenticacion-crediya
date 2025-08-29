package co.com.pragma.autenticacion.usecase.user;

import co.com.pragma.autenticacion.model.auth.AuthConstants;
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
                userRepository.existsByEmail(user.getEmail()),   // T1: ¿existe email?
                userRepository.existsByDocument(user.getIdentityDocument()) // T2: ¿existe documento?
        ).flatMap(tuple -> {
            boolean emailExists = tuple.getT1();
            boolean docExists = tuple.getT2();

            if (emailExists) return Mono.error(new DuplicateException(AuthConstants.MSG_DUPLICATE_EMAIL));
            if (docExists) return Mono.error(new DuplicateException(AuthConstants.MSG_DUPLICATE_DOCUMENT));

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
                .switchIfEmpty(Mono.error(new NotFoundException(AuthConstants.VALIDATION_USER_NOT_FOUND_ID + idNumber)));
    }

    // ---------------------- UPDATE ----------------------
    public Mono<User> editUser(User user) {
        return userRepository.getUserByIdNumber(user.getIdNumber())
                .switchIfEmpty(Mono.error(new NotFoundException(AuthConstants.VALIDATION_USER_NOT_FOUND_ID + user.getIdNumber())))
                .flatMap(existing -> {
                    validateUser(user); // validamos antes de editar
                    return userRepository.editUser(user);
                });
    }

    // ---------------------- DELETE ----------------------
    public Mono<Void> deleteUser(Long idNumber) {
        return userRepository.getUserByIdNumber(idNumber)
                .switchIfEmpty(Mono.error(new NotFoundException(AuthConstants.VALIDATION_USER_NOT_FOUND_ID + idNumber)))
                .flatMap(existing -> userRepository.deleteUser(idNumber));
    }

    // ---------------------- EXISTENCE CHECK ----------------------
    public Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Mono<Boolean> existsByDocument(String documentoIdentidad) {
        return userRepository.existsByDocument(documentoIdentidad);
    }

    // ---------------------- VALIDACIONES ----------------------
    private void validateUser(User user) {
        if (isNullOrEmpty(user.getName())) throw new ValidationException(AuthConstants.VALIDATION_NAME_REQUIRED);
        if (isNullOrEmpty(user.getLastName())) throw new ValidationException(AuthConstants.VALIDATION_LASTNAME_REQUIRED);

        validateEmail(user.getEmail());
        validateSalary(user.getBaseSalary());
        validateDateOfBirth(user.getDateOfBirth());
    }

    private void validateEmail(String email) {
        if (isNullOrEmpty(email)) throw new ValidationException(AuthConstants.VALIDATION_EMAIL_REQUIRED);
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$"))
            throw new ValidationException(AuthConstants.VALIDATION_EMAIL_FORMAT);
    }

    private void validateSalary(BigDecimal salario) {
        if (salario == null) throw new ValidationException(AuthConstants.VALIDATION_SALARY_REQUIRED);
        if (salario.compareTo(BigDecimal.ZERO) < 0 || salario.compareTo(new BigDecimal("1500000")) > 0)
            throw new ValidationException(AuthConstants.VALIDATION_SALARY_RANGE);
    }

    private void validateDateOfBirth(String dateOfBirth) {
        if (isNullOrEmpty(dateOfBirth)) throw new ValidationException(AuthConstants.VALIDATION_DOB_REQUIRED);
        try {
            LocalDate fecha = LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int edad = Period.between(fecha, LocalDate.now()).getYears();
            if (edad < 18) throw new ValidationException(AuthConstants.VALIDATION_DOB_UNDERAGE);
        } catch (DateTimeParseException e) {
            throw new ValidationException(AuthConstants.VALIDATION_DOB_FORMAT);
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
