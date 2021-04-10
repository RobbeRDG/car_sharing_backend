package be.kul.carservice.utils.json.jsonObjects.amqpMessages.ride;

import be.kul.carservice.entity.Ride;
import be.kul.carservice.utils.helperObjects.RideStateEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RideEnd {
    private long rideId;
    private RideStateEnum currentState;
    private Timestamp finishedOn;
    private Timestamp lastStateUpdate;

    public RideEnd(Ride ride) {
        this.rideId = ride.getRideId();
        this.currentState = ride.getCurrentState();
        this.finishedOn = ride.getFinishedOn();
        this.lastStateUpdate = ride.getLastStateUpdate();
    }
}
