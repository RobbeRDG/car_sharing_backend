package be.kul.billingservice.controller.amqp;

import be.kul.billingservice.entity.Bill;
import be.kul.billingservice.service.BillingService;
import be.kul.billingservice.utils.amqp.RabbitMQConfig;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.UserPaymentMethodUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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

    public void sendUserPaymentMethodUpdate(UserPaymentMethodUpdate userPaymentMethodUpdate) throws JsonProcessingException {
        //Convert userPaymentMethodUpdate to json
        String userPaymentMethodUpdateString = objectMapper.writeValueAsString(userPaymentMethodUpdate);

        //Send the paymentInitialisationString to the payment service
        internalRabbitTemplate.convertAndSend(
                RabbitMQConfig.SERVER_TO_SERVER_EXCHANGE,
                RabbitMQConfig.USER_PAYMENT_METHOD_UPDATE_BINDING_KEY,
                userPaymentMethodUpdateString
        );
    }

    public void sendBillToPaymentProcessor(Bill bill) {
        try {
            //Convert bill to json
            String billString = objectMapper.writeValueAsString(bill);

            //Send the billString to payment processing
            internalRabbitTemplate.convertAndSend(
                    RabbitMQConfig.SERVER_TO_SERVER_EXCHANGE,
                    RabbitMQConfig.BILL_PROCESSING_BINDING_KEY,
                    billString
            );
        } catch (JsonProcessingException e) {
            BillingService.logger.error("Couldn't send bill to payment processor: " + e.getLocalizedMessage());
        }
    }
}
