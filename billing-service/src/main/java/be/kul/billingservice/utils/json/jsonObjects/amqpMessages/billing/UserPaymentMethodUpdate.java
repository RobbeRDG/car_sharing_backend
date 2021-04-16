package be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing;

import be.kul.billingservice.utils.helperObjects.UserPaymentMethodStatusEnum;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.AmqpMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserPaymentMethodUpdate extends AmqpMessage {
    private UserPaymentMethodStatusEnum userPaymentMethodStatus;
}
