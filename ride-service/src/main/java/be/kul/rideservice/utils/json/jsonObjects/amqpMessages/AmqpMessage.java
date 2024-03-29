package be.kul.rideservice.utils.json.jsonObjects.amqpMessages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class AmqpMessage {
    private LocalDateTime messageCreationTimestamp;

    public AmqpMessage() {
        this.messageCreationTimestamp = LocalDateTime.now();
    }

}
