package co.com.pragma.autenticacion.usecase.exceptions;
/**
 * Excepción para validaciones de negocio.
 */
public class ValidationException extends DomainException {

    private static final String CODE = "VALIDATION_ERROR";

    public ValidationException(String message) {
        super(CODE, message);
    }
}