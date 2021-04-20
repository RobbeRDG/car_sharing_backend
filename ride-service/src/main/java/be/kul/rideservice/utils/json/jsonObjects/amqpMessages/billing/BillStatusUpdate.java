package be.kul.rideservice.utils.json.jsonObjects.amqpMessages.billing;

import be.kul.rideservice.utils.helperObjects.BillStatusEnum;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.AmqpMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class BillStatusUpdate extends AmqpMessage {
    private long billId;
    private long rideId;
    private BillStatusEnum billStatus;
    private LocalDateTime createdOn;
}
