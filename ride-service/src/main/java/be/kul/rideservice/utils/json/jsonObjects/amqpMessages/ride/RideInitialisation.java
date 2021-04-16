package be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride;


import be.kul.rideservice.utils.helperObjects.RideStateEnum;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.AmqpMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RideInitialisation extends AmqpMessage {
    private long rideId;
    private long carId;
    private String userId;
    private RideStateEnum currentState;
    private LocalDateTime createdOn;
    private LocalDateTime startedOn;
    private LocalDateTime finishedOn;
    private LocalDateTime lastStateUpdate;

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

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public LocalDateTime getStartedOn() {
        return startedOn;
    }

    public LocalDateTime getFinishedOn() {
        return finishedOn;
    }

    public LocalDateTime getLastStateUpdate() {
        return lastStateUpdate;
    }
}
