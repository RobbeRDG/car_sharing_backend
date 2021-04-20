package be.kul.carservice.entity;

import be.kul.carservice.utils.json.jsonObjects.amqpMessages.payment.UserPaymentMethodStatusUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class UserPaymentMethodStatus {
    @Id
    private String userId;
    @NotNull
    private boolean hasUserValidPaymentMethod;
    @NotNull
    private LocalDateTime lastStateUpdate;

    public UserPaymentMethodStatus(String userId) {
        this.userId = userId;
        this.hasUserValidPaymentMethod = false;
        this.lastStateUpdate = LocalDateTime.now();
    }

    public void updatePaymentMethodStatus(UserPaymentMethodStatusUpdate userPaymentMethodStatusUpdate) {
        this.hasUserValidPaymentMethod = userPaymentMethodStatusUpdate.hasUserValidPaymentMethod();
        this.lastStateUpdate = userPaymentMethodStatusUpdate.getMessageCreationTimestamp();
    }

    public boolean hasUserValidPaymentMethod() {
        return hasUserValidPaymentMethod;
    }
}
