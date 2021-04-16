package be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride;

import be.kul.rideservice.utils.helperObjects.RideStateEnum;
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
public class RideEnd {
    private long rideId;
    private RideStateEnum currentState;
    private LocalDateTime finishedOn;
    private LocalDateTime lastStateUpdate;

    public long getRideId() {
        return rideId;
    }

    public RideStateEnum getCurrentState() {
        return currentState;
    }

    public LocalDateTime getFinishedOn() {
        return finishedOn;
    }

    public LocalDateTime getLastStateUpdate() {
        return lastStateUpdate;
    }
}
