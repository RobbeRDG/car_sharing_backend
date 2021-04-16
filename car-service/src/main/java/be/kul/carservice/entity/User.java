package be.kul.carservice.entity;

import be.kul.carservice.utils.helperObjects.PaymentMethodStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class User {
    @Id
    private String userId;
    @NotNull
    private PaymentMethodStatusEnum paymentMethodStatus;

    public PaymentMethodStatusEnum getPaymentMethodStatus() {
        return paymentMethodStatus;
    }
}
