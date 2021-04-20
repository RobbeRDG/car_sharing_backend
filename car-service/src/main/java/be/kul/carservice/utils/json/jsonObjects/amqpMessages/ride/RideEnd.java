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
public class RideEnd extends AmqpMessage {
    private long rideId;
    private RideStatusEnum currentStatus;
    private LocalDateTime finishedOn;
    private LocalDateTime createdOn;


    public RideEnd(Ride ride) {
        this.rideId = ride.getRideId();
        this.currentStatus = ride.getCurrentStatus();
        this.finishedOn = ride.getFinishedOn();
        this.createdOn = ride.getLastStatusUpdate();
    }
}
