package be.kul.billingservice.service;

import be.kul.billingservice.utils.json.jsonObjects.amqpMessages.payment.BillInitialisation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BillingService {

    public ResponseEntity<String> configureNewPaymentMethod(String userId) {
        
    }

    public void addBill(BillInitialisation billInitialisation) {
    }
}
