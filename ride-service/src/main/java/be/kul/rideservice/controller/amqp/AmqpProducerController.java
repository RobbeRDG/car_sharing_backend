package be.kul.rideservice.controller.amqp;

import be.kul.rideservice.service.RideService;
import be.kul.rideservice.utils.amqp.RabbitMQConfig;
import be.kul.rideservice.utils.json.jsonObjects.amqpMessages.payment.PaymentInitialisation;
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
    RideService rideService;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendPaymentInitialisation(PaymentInitialisation paymentInitialisation) throws JsonProcessingException {
        //Convert paymentInitialisation to json
        String paymentInitialisationString = objectMapper.writeValueAsString(paymentInitialisation);

        //Send the paymentInitialisationString to the payment service
        internalRabbitTemplate.convertAndSend(
                RabbitMQConfig.SERVER_TO_SERVER_EXCHANGE,
                RabbitMQConfig.PAYMENT_INITIALISATION_BINDING_KEY,
                paymentInitialisationString
        );
    }


}
