package co.com.pragma.autenticacion.usecase.exceptions;

/**
 * Clase base para todas las excepciones de negocio (dominio).
 *
 * - Extiende de RuntimeException para que sean "unchecked".
 * - Agregamos un `code` que sirve para identificar el tipo de error.
 *   Ej: NOT_FOUND, VALIDATION_ERROR, etc.
 */
public abstract class DomainException extends RuntimeException {

    private final String code; // 👉 código único de error

    public DomainException(String code, String message) {
        super(message); // Mensaje descriptivo
        this.code = code; // Código que nos permitirá mapear en respuestas HTTP
    }

    public String getCode() {
        return code;
    }
}
