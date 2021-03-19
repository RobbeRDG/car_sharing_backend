package be.kul.carservice.utils.exceptions;

public class ReservationCooldownException extends RuntimeException{
    public ReservationCooldownException() {
        super("Action can't be performed because of cooldown");
    }

    public ReservationCooldownException(String message) {
        super(message);
    }
}
