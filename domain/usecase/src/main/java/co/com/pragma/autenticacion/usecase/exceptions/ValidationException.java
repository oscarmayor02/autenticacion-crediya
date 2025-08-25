package co.com.pragma.autenticacion.usecase.exceptions;

/**
 * Se lanza en validaciones de negocio (ej: edad mínima, salario inválido).
 */
public class ValidationException extends DomainException {
    private static final String CODE = "VALIDATION_ERROR";

    public ValidationException(String message) {
        super(CODE, message);
    }
}
