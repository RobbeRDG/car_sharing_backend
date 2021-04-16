package be.kul.billingservice.service;

import be.kul.billingservice.controller.amqp.AmqpProducerController;
import be.kul.billingservice.entity.Bill;
import be.kul.billingservice.repository.BillRepository;
import be.kul.billingservice.utils.helperObjects.UserPaymentMethodStatusEnum;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.BillInitialisation;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.UserPaymentMethodUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BillingService {
    public static final Logger logger = LoggerFactory.getLogger(BillingService.class);

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private AmqpProducerController amqpProducerController;

    public ResponseEntity<String> configureNewPaymentMethod(String userId) throws JsonProcessingException {
        //Todo

        //Send the new userPaymentStatus to the car service
        amqpProducerController.sendUserPaymentMethodUpdate(new UserPaymentMethodUpdate(UserPaymentMethodStatusEnum.VALID));

        //return to client
        return new ResponseEntity<>(
                "Successfully set payment method",
                HttpStatus.OK
        );
    }

    public void addBill(BillInitialisation billInitialisation) {
        //Calculate the bill from the information in the billInitialisation
        Bill bill = Bill.calculateBill(billInitialisation);

        //Save the bill
        billRepository.save(bill);

        //Send the bill to the bill
        amqpProducerController.sendBillToPaymentProcessor(bill);
    }

    public void processBill(Bill bill) {
        //Todo
        logger.info("processed bill");
    }
}
