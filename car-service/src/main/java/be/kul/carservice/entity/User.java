package be.kul.carservice.entity;

import be.kul.carservice.utils.helperObjects.PaymentMethodStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class User {
    private String userId;
    private PaymentMethodStatusEnum paymentMethodStatus;

    public PaymentMethodStatusEnum getPaymentMethodStatus() {
        return paymentMethodStatus;
    }
}
