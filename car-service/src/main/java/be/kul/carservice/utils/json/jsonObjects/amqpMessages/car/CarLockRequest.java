package be.kul.carservice.utils.json.jsonObjects.amqpMessages.car;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarLockRequest extends CarRequest{
    private static final String REQUEST_TYPE = "CarLockRequest";
    private long rideId;
    private boolean lockCar;

    public CarLockRequest(long rideId, long carId, boolean lockCar, int expirationTimeInMilliseconds) {
        super(REQUEST_TYPE, carId, expirationTimeInMilliseconds);
        this.rideId = rideId;
        this.lockCar = lockCar;
    }
}
