package be.kul.carservice.utils.json.jsonObjects.amqpMessages.car;

import be.kul.carservice.entity.Ride;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarRideRequest extends CarRequest {
    private static final String REQUEST_TYPE = CarRideRequest.class.getSimpleName();
    private long rideId;

    public CarRideRequest(Ride ride, int expirationTimeInMilliseconds) {
        super(REQUEST_TYPE, ride.getCar().getCarId(), expirationTimeInMilliseconds);
        this.rideId = ride.getRideId();
    }

    public long getRideId() {
        return rideId;
    }
}
