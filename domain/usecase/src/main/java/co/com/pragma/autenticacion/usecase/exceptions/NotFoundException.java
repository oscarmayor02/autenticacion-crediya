package co.com.pragma.autenticacion.usecase.exceptions;

/**
 * Excepción cuando no se encuentra un recurso (usuario, rol, etc.).
 */
public class NotFoundException extends DomainException {

    private static final String CODE = "NOT_FOUND";

    public NotFoundException(String message) {
        super(CODE, message);
    }
}