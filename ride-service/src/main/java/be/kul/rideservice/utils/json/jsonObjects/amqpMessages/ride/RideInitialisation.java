package be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride;


import be.kul.rideservice.utils.helperObjects.RideStateEnum;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.AmqpMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RideInitialisation extends AmqpMessage {
    private long rideId;
    private long carId;
    private String userId;
    private RideStateEnum currentState;
    private Timestamp createdOn;
    private Timestamp startedOn;
    private Timestamp finishedOn;
    private Timestamp lastStateUpdate;

    public long getRideId() {
        return rideId;
    }

    public long getCarId() {
        return carId;
    }

    public String getUserId() {
        return userId;
    }

    public RideStateEnum getCurrentState() {
        return currentState;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public Timestamp getStartedOn() {
        return startedOn;
    }

    public Timestamp getFinishedOn() {
        return finishedOn;
    }

    public Timestamp getLastStateUpdate() {
        return lastStateUpdate;
    }
}
