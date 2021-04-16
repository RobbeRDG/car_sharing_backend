package be.kul.carservice.utils.json.jsonObjects.amqpMessages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter
@Setter
public class AmqpMessage {
    private Timestamp messageCreationTimestamp;

    public AmqpMessage() {
        this.messageCreationTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public Timestamp getMessageCreationTimestamp() {
        return messageCreationTimestamp;
    }
}
