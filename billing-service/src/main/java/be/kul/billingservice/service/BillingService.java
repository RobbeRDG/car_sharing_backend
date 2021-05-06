package be.kul.billingservice.service;

import be.kul.billingservice.controller.amqp.AmqpProducerController;
import be.kul.billingservice.entity.Bill;
import be.kul.billingservice.entity.UserPaymentInformation;
import be.kul.billingservice.repository.BillRepository;
import be.kul.billingservice.repository.UserPaymentInformationRepository;
import be.kul.billingservice.utils.exceptions.DoesntExistException;
import be.kul.billingservice.utils.exceptions.ValidPaymentMethodException;
import be.kul.billingservice.utils.exceptions.NotAllowedException;
import be.kul.billingservice.utils.exceptions.SomethingWentWrongException;
import be.kul.billingservice.utils.helperObjects.BillStatusEnum;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.BillInitialisation;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.BillStatusUpdate;
import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.billing.UserPaymentMethodStatusUpdate;
import be.kul.billingservice.utils.json.rest.ClientSecret;
import be.kul.billingservice.utils.json.rest.PaymentMethodConfirmation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodListParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public PaymentMethodConfirmation checkUserPaymentMethod(String userId) {
        try {
            boolean successfullyConfiguredPaymentMethod = false;

            Stripe.apiKey = stripePublicKey;

            //Get the corresponding userPaymentInformation
            UserPaymentInformation userPaymentInformation = userPaymentInformationRepository.findById(userId).orElse(null);
            if (userPaymentInformation == null)
                throw new DoesntExistException("Couldn't check the user payment information: The user with id '" + userId + "' doesn't exist");

            //Get the stripe customerId
            String stripeCustomerId = userPaymentInformation.getStripeCustomerId();

            //Get the user payment methods
            PaymentMethodListParams paymentMethodListParams =
                    PaymentMethodListParams.builder()
                            .setCustomer(stripeCustomerId)
                            .setType(PaymentMethodListParams.Type.CARD)
                            .setLimit(1L)
                            .build();
            PaymentMethodCollection paymentMethods = PaymentMethod.list(paymentMethodListParams);

            log.info(paymentMethods.toJson());

            //Get the first payment method
            PaymentMethod paymentMethod = paymentMethods.getData().get(0);
            if (paymentMethod != null) successfullyConfiguredPaymentMethod = true;

            //If the user has pending payments, pay them now
            try {
                ArrayList<Long> failedBillIds = getUserFailedBillIds(userId);
                if (!failedBillIds.isEmpty() && successfullyConfiguredPaymentMethod) {
                    for (long billId : failedBillIds) {
                        processBill(billId);
                    }
                }
            } catch (Exception e) {
                successfullyConfiguredPaymentMethod = false;
            }

            log.info("User payment information for user with id'" + userId + "' is successfully configured");

            //Send the new userPaymentStatus to the car service
            UserPaymentMethodStatusUpdate userPaymentMethodStatusUpdate = new UserPaymentMethodStatusUpdate(userId, successfullyConfiguredPaymentMethod);
            amqpProducerController.sendUserPaymentMethodUpdate(userPaymentMethodStatusUpdate);

            //return to client
            if (successfullyConfiguredPaymentMethod) {
                String confirmationMessage = "User payment information is successfully set";
                return new PaymentMethodConfirmation(true, confirmationMessage);
            } else {
                String confirmationMessage = "User payment information has not been set";
                return new PaymentMethodConfirmation(false, confirmationMessage);
            }
        } catch (Exception e) {
            log.error("Failed to check payment method: " + e.getLocalizedMessage());
            throw new SomethingWentWrongException("Couldn't check payment method: something went wrong");
        }
    }

    private ArrayList<Long> getUserFailedBillIds(String userId) {
        //Get all failed bills
        ArrayList<Bill> failedBills = billRepository.getUserFailedBills(userId);

        //Get the id's from all the failed bills
        ArrayList<Long> failedBillIds = new ArrayList<>();
        for (Bill bill : failedBills) {
            failedBillIds.add(bill.getBillId());
        }

        return failedBillIds;
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

    public void processBill(long billId) throws StripeException {
        //Get the requested bill
        Bill bill = billRepository.findById(billId).orElse(null);
        if (bill==null) throw new DoesntExistException("Couldn't process the bill: The bill with id '" + billId + "' doesn't exist");

        //Get the corresponding userPaymentInformation
        String userId = bill.getUserId();
        UserPaymentInformation userPaymentInformation = userPaymentInformationRepository.findById(userId).orElse(null);
        if (userPaymentInformation==null) throw new DoesntExistException("Couldn't process the bill: The user with id '" + userId + "' doesn't exist");

        //Get the stripe customerId
        String stripeCustomerId = userPaymentInformation.getStripeCustomerId();

        //Get the billAmount in cents from the bill
        double billTotalAmountInEur = bill.getBillTotalAmountInEur();
        long billTotalAmountInEurCents = (long) Math.ceil(billTotalAmountInEur * 100);

        //Try to process the payment
        PaymentMethod paymentMethod = null;
        try {
            Stripe.apiKey = stripePublicKey;

            //Get the user payment methods
            PaymentMethodListParams paymentMethodListParams =
                    PaymentMethodListParams.builder()
                            .setCustomer(stripeCustomerId)
                            .setType(PaymentMethodListParams.Type.CARD)
                            .setLimit(1L)
                            .build();
            PaymentMethodCollection paymentMethods = PaymentMethod.list(paymentMethodListParams);

            //Get the first payment methodId
            paymentMethod = paymentMethods.getData().get(0);
            if (paymentMethod == null)
                throw new ValidPaymentMethodException("Couldn't process the bill: The user with id '" + userId + "'  doesn't have a valid payment method");
            String paymentMethodId = paymentMethod.getId();

            //Bill the user payment method
            PaymentIntentCreateParams paymentIntentParams =
                    PaymentIntentCreateParams.builder()
                            .setCurrency("eur")
                            .setAmount(billTotalAmountInEurCents)
                            .setPaymentMethod(paymentMethodId)
                            .setCustomer(stripeCustomerId)
                            .setConfirm(true)
                            .setOffSession(true)
                            .build();
            PaymentIntent.create(paymentIntentParams);
        } catch (ValidPaymentMethodException e) {
            //Handle the failed payment
            handleBillPaymentException(e, bill);

            throw e;
        } catch (StripeException e) {
            //Detach the user payment method
            paymentMethod.detach();

            //Handle the failed payment
            handleBillPaymentException(e, bill);

            throw e;
        }

        //Set the new bill state
        bill.setBillStatus(BillStatusEnum.PAID);
        bill.setLastStateUpdate(LocalDateTime.now());

        //Save the bill
        Bill savedBill = billRepository.save(bill);

        //Send the ride service the billStateUpdate
        try {
            BillStatusUpdate billStatusUpdate = new BillStatusUpdate(savedBill);
            amqpProducerController.sendBillStatusUpdate(billStatusUpdate);
        } catch (JsonProcessingException e) {
            log.error("Failed to send bill status update: " + e.getLocalizedMessage());
        }

        log.info("processed bill with id '" + billId + "'");
    }

    private void handleBillPaymentException(Exception e, Bill bill) {
        //Set the new bill state
        bill.setBillStatus(BillStatusEnum.FAILED);
        bill.setLastStateUpdate(LocalDateTime.now());

        //Save the bill
        Bill savedBill = billRepository.save(bill);

        //Invalidate the user payment method
        String userId = bill.getUserId();
        UserPaymentMethodStatusUpdate userPaymentMethodStatusUpdate = new UserPaymentMethodStatusUpdate(userId, false);
        try {
            amqpProducerController.sendUserPaymentMethodUpdate(userPaymentMethodStatusUpdate);
        } catch (JsonProcessingException err) {
            log.error("Failed to send payment method update: " + e.getLocalizedMessage());
        }

        //Send the billStateUpdate
        try {
            BillStatusUpdate billStatusUpdate = new BillStatusUpdate(savedBill);
            amqpProducerController.sendBillStatusUpdate(billStatusUpdate);
        } catch (JsonProcessingException err) {
            log.error("Failed to send bill status update: " + e.getLocalizedMessage());
        }
    }

    public ClientSecret initialiseNewUserPaymentMethod(String userId) throws StripeException {
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

        log.info("Started card setup process for " + userId);

        //return the intent clientSecret to the client
        return new ClientSecret(setupIntent.getClientSecret());
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
