package be.kul.billingservice.utils.exceptions;

public class ValidPaymentMethodException extends RuntimeException{
    public ValidPaymentMethodException() {
        super("There is no valid payment method set");
    }

    public ValidPaymentMethodException(String message) {
        super(message);
    }
}
