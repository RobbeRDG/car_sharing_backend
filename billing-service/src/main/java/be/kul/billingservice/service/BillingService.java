package be.kul.billingservice.service;

import be.kul.billingservice.controller.amqp.AmqpProducerController;
import be.kul.billingservice.entity.Bill;
import be.kul.billingservice.entity.UserPaymentInformation;
import be.kul.billingservice.repository.BillRepository;
import be.kul.billingservice.repository.UserPaymentInformationRepository;
import be.kul.billingservice.utils.exceptions.DoesntExistException;
import be.kul.billingservice.utils.exceptions.NotAllowedException;
import be.kul.billingservice.utils.exceptions.SomethingWentWrongException;
import be.kul.billingservice.utils.helperObjects.BillStatusEnum;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.BillInitialisation;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.BillStatusUpdate;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.UserPaymentMethodStatusUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.SetupIntent;
import com.stripe.param.CustomerCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class BillingService {
    @Value("${spring.configurations.stripe.public_key}")
    private String stripePublicKey;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UserPaymentInformationRepository userPaymentInformationRepository;

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

    public ResponseEntity<String> initialiseNewUserPaymentMethod(String userId) throws StripeException {
        //Set the stripe key
        Stripe.apiKey = stripePublicKey;

        //Get the user payment information
        UserPaymentInformation userPaymentInformation = userPaymentInformationRepository.findById(userId).orElse(null);
        if (userPaymentInformation==null) {
            userPaymentInformation = createNewUserPaymentInformation(userId);
        }

        //Get the stripeCustomerId from the userPaymentInformation
        String stripeCustomerId = userPaymentInformation.getStripeCustomerId();

        //Create a stripe setupintent
        Map<String, Object> params = new HashMap<>();
        params.put("customer", stripeCustomerId);
        SetupIntent setupIntent = SetupIntent.create(params);

        //return the intent clientSecret to the client
        return new ResponseEntity<>(
                setupIntent.getClientSecret(),
                HttpStatus.OK
        );
    }

    private UserPaymentInformation createNewUserPaymentInformation(String userId) throws StripeException {
        //Create a new Stripe customer with the corresponding userId in the description
        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .setDescription(userId)
                        .build();
        Customer stripeCustomer = Customer.create(params);

        //Create a new UserPaymentInformation object
        String stripeCustomerID = stripeCustomer.getId();
        UserPaymentInformation userPaymentInformation = new UserPaymentInformation(userId,stripeCustomerID);

        return userPaymentInformationRepository.save(userPaymentInformation);
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
