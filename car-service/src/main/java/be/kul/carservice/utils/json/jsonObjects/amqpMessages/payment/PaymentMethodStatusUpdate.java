package be.kul.carservice.utils.json.jsonObjects.amqpMessages.payment;

import be.kul.carservice.utils.helperObjects.PaymentMethodStatusEnum;
import be.kul.carservice.utils.json.jsonObjects.amqpMessages.AmqpMessage;

public class PaymentMethodStatusUpdate extends AmqpMessage {
    private String userId;
    private PaymentMethodStatusEnum paymentMethodStatus;

    public PaymentMethodStatusUpdate(String userId, PaymentMethodStatusEnum paymentMethodStatus) {
        super();
        this.userId = userId;
        this.paymentMethodStatus = paymentMethodStatus;
    }

    public String getUserId() {
        return userId;
    }
}
