package be.kul.carservice.utils.json.jsonObjects.amqpMessages.payment;

import be.kul.carservice.utils.json.jsonObjects.amqpMessages.AmqpMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserPaymentMethodStatusUpdate extends AmqpMessage {
    private String userId;
    private boolean hasUserValidPaymentMethod;

    public boolean hasUserValidPaymentMethod() {
        return hasUserValidPaymentMethod;
    }
}

