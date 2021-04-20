package be.kul.billingservice.controller.amqp;

import be.kul.billingservice.entity.Bill;
import be.kul.billingservice.service.BillingService;
import be.kul.billingservice.utils.amqp.RabbitMQConfig;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.BillStatusUpdate;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.UserPaymentMethodStatusUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class AmqpProducerController {
    @Resource(name = "internalRabbitTemplate")
    private RabbitTemplate internalRabbitTemplate;

    @Resource(name="internalRabbitAdmin")
    private RabbitAdmin internalRabbitAdmin;

    @Autowired
    BillingService billingService;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendUserPaymentMethodUpdate(UserPaymentMethodStatusUpdate userPaymentMethodStatusUpdate) throws JsonProcessingException {
        //Convert userPaymentMethodUpdate to json
        String userPaymentMethodStatusUpdateString = objectMapper.writeValueAsString(userPaymentMethodStatusUpdate);

        //Send the paymentInitialisationString to the payment service
        internalRabbitTemplate.convertAndSend(
                RabbitMQConfig.INTERNAL_EXCHANGE,
                RabbitMQConfig.USER_PAYMENT_METHOD_UPDATE_BINDING_KEY,
                userPaymentMethodStatusUpdateString
        );
    }

    public void sendBillToPaymentProcessor(long billId) {
        //Convert billId to string
        String billIdString = String.valueOf(billId);

        //Send the billString to payment processing
        internalRabbitTemplate.convertAndSend(
                RabbitMQConfig.INTERNAL_EXCHANGE,
                RabbitMQConfig.BILL_PROCESSING_BINDING_KEY,
                billIdString
        );
    }

    public void sendBillStatusUpdate(BillStatusUpdate billStatusUpdate) throws JsonProcessingException {
        //Convert billStatusUpdate to json
        String billStatusUpdateString = objectMapper.writeValueAsString(billStatusUpdate);

        //Send the billString to payment processing
        internalRabbitTemplate.convertAndSend(
                RabbitMQConfig.INTERNAL_EXCHANGE,
                RabbitMQConfig.BILL_STATUS_UPDATE_BINDING_KEY,
                billStatusUpdateString
        );
    }
}
