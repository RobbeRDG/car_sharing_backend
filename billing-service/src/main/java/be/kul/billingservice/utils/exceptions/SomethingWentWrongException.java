package be.kul.billingservice.utils.exceptions;

public class SomethingWentWrongException extends RuntimeException{
    public SomethingWentWrongException() {
        super("Something went wrong");
    }

    public SomethingWentWrongException(String message) {
        super(message);
    }
}
