package be.kul.carservice.utils.json.jsonObjects.amqpMessages.car;

import be.kul.carservice.utils.json.jsonObjects.amqpMessages.AmqpMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
public class CarAcknowledgement extends AmqpMessage {
    private static String messageType = CarAcknowledgement.class.getSimpleName();
    private long carId;
    private long rideId;
    private boolean confirmAcknowledge;
    private String errorMessage;

    public CarAcknowledgement(long carId, long rideId, boolean confirmAcknowledge, String errorMessage) {
        super();
        this.carId = carId;
        this.rideId = rideId;
        this.confirmAcknowledge = confirmAcknowledge;
        this.errorMessage = errorMessage;
    }

    public long getCarId() {
        return carId;
    }

    public void setCarId(long carId) {
        this.carId = carId;
    }

    public long getRideId() {
        return rideId;
    }

    public void setRideId(long rideId) {
        this.rideId = rideId;
    }

    public boolean isConfirmAcknowledge() {
        return confirmAcknowledge;
    }

    public void setConfirmAcknowledge(boolean confirmAcknowledge) {
        this.confirmAcknowledge = confirmAcknowledge;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


    public <T extends CarRequest> boolean confirmsRequest(T request) {
        return (carId==request.getCarId() && confirmAcknowledge);
    }
}
