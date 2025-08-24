package co.com.pragma.autenticacion.usecase.exceptions;

/**
 * Clase base para excepciones de dominio.
 */
public abstract class DomainException extends RuntimeException {

    private final String code;

    public DomainException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}