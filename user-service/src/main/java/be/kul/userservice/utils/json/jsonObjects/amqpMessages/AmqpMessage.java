package be.kul.userservice.utils.json.jsonObjects.amqpMessages;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class AmqpMessage {
    private Timestamp messageCreationTimestamp;

    public AmqpMessage() {
        this.messageCreationTimestamp = new Timestamp(System.currentTimeMillis());
    }
}
