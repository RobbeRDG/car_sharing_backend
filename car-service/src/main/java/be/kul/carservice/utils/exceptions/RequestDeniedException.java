package be.kul.carservice.utils.exceptions;

public class RequestDeniedException extends RuntimeException{
    public RequestDeniedException() {
        super("The request has been denied");
    }

    public RequestDeniedException(String message) {
        super(message);
    }
}
