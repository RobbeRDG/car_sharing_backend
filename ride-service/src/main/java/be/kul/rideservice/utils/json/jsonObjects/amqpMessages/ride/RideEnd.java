package be.kul.rideservice.utils.json.jsonObjects.amqpMessages.ride;

import be.kul.rideservice.utils.helperObjects.RideStatusEnum;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.AmqpMessage;
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
}
