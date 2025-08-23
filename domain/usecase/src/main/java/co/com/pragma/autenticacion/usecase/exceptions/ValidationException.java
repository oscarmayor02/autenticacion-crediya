
package co.com.pragma.autenticacion.usecase.exceptions;
import java.util.List;

public class ValidationException extends DomainException {
    private final List<String> errors;
    public ValidationException(List<String> errors) {
        super("VALIDATION_ERROR", "Error de validación");
        this.errors = errors;
    }
    public List<String> getErrors() { return errors; }
}