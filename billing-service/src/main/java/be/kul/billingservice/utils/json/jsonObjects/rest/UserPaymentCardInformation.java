package be.kul.billingservice.utils.json.jsonObjects.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserPaymentCardInformation {
    private boolean hasACardConfigured;
    private String cardNumber;
}
