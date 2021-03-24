package be.kul.rideservice.utils.exceptions;

public class DoesntExistException extends RuntimeException {
    public DoesntExistException() {
        super("The resource doesn't exists");
    }

    public DoesntExistException(String message) {
        super(message);
    }
}
