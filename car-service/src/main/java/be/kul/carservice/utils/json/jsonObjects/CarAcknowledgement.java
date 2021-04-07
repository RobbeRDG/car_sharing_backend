package be.kul.carservice.utils.json.jsonObjects;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
public class CarAcknowledgement {
    private Timestamp createdOn;
    private long carId;
    private long rideId;
    private boolean confirmAcknowledge;
    private String message;

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
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

    public boolean confirmsRideRequest(CarRideRequest rideRequest) {
        return (carId==rideRequest.getCarId() && confirmAcknowledge);
    }

    public boolean confirmsLockRequest(CarLockRequest carLockRequest) {
        return (carId==carLockRequest.getCarId() && confirmAcknowledge);
    }
}
