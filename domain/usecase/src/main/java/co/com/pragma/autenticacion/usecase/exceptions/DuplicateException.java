package co.com.pragma.autenticacion.usecase.exceptions;

/**
 * Se lanza cuando encontramos datos duplicados (ej: email ya existe).
 */
public class DuplicateException extends DomainException {
    private static final String CODE = "DUPLICATE_ERROR";

    public DuplicateException(String message) {
        super(CODE, message);
    }
}
