package be.kul.billingservice.controller.amqp;

import be.kul.billingservice.service.BillingService;
import be.kul.billingservice.utils.amqp.RabbitMQConfig;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.BillInitialisation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;

@Slf4j
public class AmqpConsumerController {
    @Autowired
    BillingService billingService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.BILL_INITIALISATION_QUEUE, containerFactory = "internalRabbitListenerContainerFactory")
    public void addRide(@Payload String billInitialisationString) throws JsonProcessingException {
        //get the billInitialisation object from the string
        BillInitialisation billInitialisation = objectMapper.readValue(billInitialisationString, BillInitialisation.class);

        //Handle the bill initialisation
        billingService.addBill(billInitialisation);
    }

    @RabbitListener(queues = RabbitMQConfig.BILL_PROCESSING_QUEUE, containerFactory = "internalRabbitListenerContainerFactory")
    public void processBill(@Payload String billIdString) {
        //get the Bill object from the string
        long billId = Long.parseLong(billIdString);

        //Handle processing of the bill
        try {
            billingService.processBill(billId);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }
}
