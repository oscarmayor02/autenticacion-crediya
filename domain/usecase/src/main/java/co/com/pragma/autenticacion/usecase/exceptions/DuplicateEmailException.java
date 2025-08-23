package co.com.pragma.autenticacion.usecase.exceptions;
public class DuplicateEmailException extends DomainException {
    public DuplicateEmailException(String email) {
        super("EMAIL_ALREADY_EXISTS", "El correo ya está registrado: " + email);
    }
}