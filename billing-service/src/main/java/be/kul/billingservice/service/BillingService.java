package be.kul.billingservice.service;

import be.kul.billingservice.controller.amqp.AmqpProducerController;
import be.kul.billingservice.entity.Bill;
import be.kul.billingservice.repository.BillRepository;
import be.kul.billingservice.utils.exceptions.DoesntExistException;
import be.kul.billingservice.utils.exceptions.NotAllowedException;
import be.kul.billingservice.utils.exceptions.SomethingWentWrongException;
import be.kul.billingservice.utils.helperObjects.BillStatusEnum;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.BillInitialisation;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.BillStatusUpdate;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.UserPaymentMethodStatusUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class BillingService {
    @Autowired
    private BillRepository billRepository;

    @Autowired
    private AmqpProducerController amqpProducerController;

    public ResponseEntity<String> configureNewPaymentMethod(String userId) {
        //Todo

        boolean SuccessfullyConfiguredPaymentMethod = true;

        //Send the new userPaymentStatus to the car service
        UserPaymentMethodStatusUpdate userPaymentMethodStatusUpdate = new UserPaymentMethodStatusUpdate(userId, SuccessfullyConfiguredPaymentMethod);
        try {
            amqpProducerController.sendUserPaymentMethodUpdate(userPaymentMethodStatusUpdate);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new SomethingWentWrongException("Couldn't configure the payment method: something went wrong");
        }

        log.info(String.valueOf(userPaymentMethodStatusUpdate.hasUserValidPaymentMethod()));

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
        Bill savedBill = billRepository.save(bill);

        //Send a billStatusUpdate
        BillStatusUpdate billStatusUpdate = new BillStatusUpdate(savedBill);
        try {
            amqpProducerController.sendBillStatusUpdate(billStatusUpdate);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

        //Send the billId to the payment processor queue
        amqpProducerController.sendBillToPaymentProcessor(savedBill.getBillId());
    }

    public void processBill(long billId) {
        //Get the requested bill
        Bill bill = billRepository.findById(billId).orElse(null);
        if (bill==null) throw new DoesntExistException("Couldn't get the requested bill: The bill with id '" + billId + "' doesn't exist");

        //Sleep
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        //Set the new bill state
        bill.setBillStatus(BillStatusEnum.PAID);
        bill.setLastStateUpdate(LocalDateTime.now());

        //Save the bill
        Bill savedBill = billRepository.save(bill);

        //Send the ride service the billStateUpdate
        BillStatusUpdate billStatusUpdate = new BillStatusUpdate(savedBill);
        try {
            amqpProducerController.sendBillStatusUpdate(billStatusUpdate);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

        log.info("processed bill with id '" + billId + "'");
    }

    public ResponseEntity<String> initialiseNewUserPaymentMethod(String userId) {
        //TODO
        //return to client
        return new ResponseEntity<>(
                "Successfully initialised payment method",
                HttpStatus.OK
        );
    }

    public Bill getBill(String userId, long billId) {
        //Get the requested bill
        Bill bill = billRepository.findById(billId).orElse(null);
        if (bill==null) throw new DoesntExistException("Couldn't get the requested bill: The bill with id '" + billId + "' doesn't exist");

        //Check if the user owns the requested bill
        if (!bill.getUserId().equals(userId)) throw new NotAllowedException(
                "Couldn't get the requested bill: this bill does not belong to the requesting user"
        );

        //Return to client
        return bill;
    }
}
