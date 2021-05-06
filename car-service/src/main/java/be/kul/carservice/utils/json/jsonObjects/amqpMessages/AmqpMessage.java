package be.kul.carservice.utils.json.jsonObjects.amqpMessages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Component
public class AmqpMessage {
    private LocalDateTime messageCreationTimestamp;

    public AmqpMessage() {
        this.messageCreationTimestamp = LocalDateTime.now();
    }

    public LocalDateTime getMessageCreationTimestamp() {
        return messageCreationTimestamp;
    }
}
