package be.kul.billingservice.utils.json.jsonObjects.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentMethodConfirmation {
    private boolean successfullySet;
    private String message;
}
