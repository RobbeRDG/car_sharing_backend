package be.kul.billingservice.controller.amqp;

import be.kul.billingservice.service.BillingService;
import be.kul.billingservice.utils.amqp.RabbitMQConfig;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.BillInitialisation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;

public class AmqpConsumerController {
    @Autowired
    BillingService billingService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.BILL_INITIALISATION_QUEUE, containerFactory = "internalRabbitListenerContainerFactory")
    public void addRide(@Payload String billInitialisationString) throws JsonProcessingException {
        //get the rideCreation object from the string
        BillInitialisation billInitialisation = objectMapper.readValue(billInitialisationString, BillInitialisation.class);

        //Handle the ride initialisation
        billingService.addBill(billInitialisation);
    }
}
