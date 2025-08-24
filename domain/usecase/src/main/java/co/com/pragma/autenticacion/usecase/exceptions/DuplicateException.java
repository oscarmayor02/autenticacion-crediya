package co.com.pragma.autenticacion.usecase.exceptions;


/**
 * Excepción para cuando hay datos duplicados (email, documento, etc.).
 */
public class DuplicateException extends DomainException {

    private static final String CODE = "DUPLICATE_ERROR";

    public DuplicateException(String message) {
        super(CODE, message);
    }
}