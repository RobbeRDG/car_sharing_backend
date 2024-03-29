package be.kul.carservice.utils.json.jsonObjects.amqpMessages.ride;

import be.kul.carservice.entity.Ride;
import be.kul.carservice.utils.helperObjects.RideStatusEnum;
import be.kul.carservice.utils.json.jsonObjects.amqpMessages.AmqpMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RideInitialisation extends AmqpMessage {
    private long rideId;
    private long carId;
    private String userId;
    private RideStatusEnum currentState;
    private LocalDateTime createdOn;
    private LocalDateTime startedOn;
    private LocalDateTime lastStateUpdate;

    public RideInitialisation(Ride ride) {
        super();
        this.rideId = ride.getRideId();
        this.carId = ride.getCar().getCarId();
        this.userId = ride.getUserId();
        this.createdOn = ride.getCreatedOn();
        this.startedOn = ride.getStartedOn();
        this.currentState = ride.getCurrentStatus();
        this.lastStateUpdate = ride.getLastStatusUpdate();
    }
}
