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
public class RideInitialisation extends AmqpMessage {
    private long rideId;
    private long carId;
    private String userId;
    private RideStatusEnum currentState;
    private LocalDateTime createdOn;
    private LocalDateTime startedOn;
    private LocalDateTime lastStateUpdate;
}
