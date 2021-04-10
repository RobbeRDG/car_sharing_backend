package be.kul.rideservice.utils.exceptions;

public class NotAllowedException extends RuntimeException{
    public NotAllowedException() {
        super("The requested action is not allowed");
    }

    public NotAllowedException(String message) {
        super(message);
    }
}
