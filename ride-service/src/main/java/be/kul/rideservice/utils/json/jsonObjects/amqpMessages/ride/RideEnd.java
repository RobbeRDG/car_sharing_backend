package be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride;

import be.kul.rideservice.utils.helperObjects.RideStateEnum;
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

    public long getRideId() {
        return rideId;
    }

    public RideStateEnum getCurrentState() {
        return currentState;
    }

    public Timestamp getFinishedOn() {
        return finishedOn;
    }

    public Timestamp getLastStateUpdate() {
        return lastStateUpdate;
    }
}
