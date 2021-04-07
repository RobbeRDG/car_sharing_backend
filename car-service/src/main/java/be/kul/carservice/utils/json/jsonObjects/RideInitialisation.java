package be.kul.carservice.utils.json.jsonObjects;

import be.kul.carservice.entity.Ride;
import be.kul.carservice.utils.helperObjects.RideState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RideInitialisation {
    private long rideId;
    private long carId;
    private String userId;
    private Timestamp createdOn;
    private RideState currentState;

    public RideInitialisation(Ride ride) {
        this.rideId = ride.getRideId();
        this.carId = ride.getCar().getCarId();
        this.userId = ride.getUserId();
        this.createdOn = ride.getCreatedOn();
        this.currentState = ride.getCurrentState();
    }
}
