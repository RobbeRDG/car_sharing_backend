package be.kul.carservice.utils.exceptions;

public class CarOfflineException extends RuntimeException {
    public CarOfflineException() {
        super("The requested car seems to be offline");
    }

    public CarOfflineException(String message) {
        super(message);
    }
}
