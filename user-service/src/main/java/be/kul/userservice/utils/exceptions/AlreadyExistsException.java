package be.kul.userservice.utils.exceptions;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException() {
        super("The resource already exists");
    }

    public AlreadyExistsException(String message) {
        super(message);
    }
}
