package co.com.pragma.autenticacion.usecase.exceptions;

/**
 * Se lanza cuando NO encontramos un recurso (ej: usuario o rol).
 */
public class NotFoundException extends DomainException {
    private static final String CODE = "NOT_FOUND"; // constante para evitar hardcodeo

    public NotFoundException(String message) {
        super(CODE, message);
    }
}
